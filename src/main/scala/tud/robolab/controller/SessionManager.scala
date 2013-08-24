/*
 * RobolabSim
 * Copyright (C) 2013  Max Leuthaeuser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package tud.robolab.controller

import scala.collection.concurrent.TrieMap
import tud.robolab.model._
import tud.robolab.view.{Interface, SimulationView}
import tud.robolab.model.{Request, Session}
import tud.robolab.model.Client

// TODO handle multiple simultaneous connection requests
object SessionManager {
  private val sessions = new SessionPool()

  class SessionPool extends Subject[SessionPool] {
    private val peer = TrieMap[Session, SimulationView]()

    private[SessionManager] def get(s: Session) = peer(s)

    private[SessionManager] def all = peer

    private[SessionManager] def set(s: Session, v: SimulationView) {
      peer(s) = v
      notifyObservers()
    }

    private[SessionManager] def remove(s: Session) {
      Interface.removeSimTap(get(s))
      peer.remove(s)
      notifyObservers()
    }

    private[SessionManager] def block(s: Session, block: Boolean) {
      s.client.blocked = block
      notifyObservers()
    }
  }

  def getSessions(): SessionPool = sessions

  def numberOfSessions(): Int = sessions.all.size

  def hasSessions(): Boolean = sessions.all.isEmpty

  def getView(s: Session): SimulationView = sessions.get(s)

  def set(s: Session, v: SimulationView) {
    sessions.set(s, v)
  }

  def getSession(ip: String): Option[Session] = sessions.all.keys.find(_.client.ip.equals(ip))

  def getSession(i: Int): Session = {
    assert(i >= 0 && i < sessions.all.size)
    sessions.all.keys.toSeq(i)
  }

  def hasSession(ip: String): Boolean = sessions.all.keys.exists(_.client.ip.equals(ip))

  private def sessionBlocked(ip: String): Boolean = if (hasSession(ip)) getSession(ip).get.client.blocked else false

  def removeSession(ip: String) {
    if (hasSession(ip)) {
      val s = getSession(ip).get
      sessions.remove(s)
    }
  }

  def removeSession(s: Session) {
    sessions.remove(s)
  }

  def addSession(s: Session) {
    if (!hasSession(s.client.ip)) {
      val v = new SimulationView(s)
      v.isShown = false
      sessions.set(s, v)
      s.addPoint(0, 0)
    }
  }

  def addSession(ip: String): Boolean = {
    if (!hasSession(ip) && !sessionBlocked(ip)) {
      val s = Session(Client(ip), Maze.empty, Seq.empty)
      val v = new SimulationView(s)
      if (Interface.addSimTab(v, ip)) {
        sessions.set(s, v)
        s.addPoint(0, 0)
        v.updateSession()
        return true
      }
    }
    false
  }

  def blockSession(ip: String, block: Boolean = true) {
    if (!hasSession(ip)) {
      val s = Session(Client(ip), Maze.empty, Seq.empty)
      val v = new SimulationView(s)
      v.isShown = false
      sessions.set(s, v)
      s.addPoint(0, 0)
      v.updateSession()
    }
    sessions.block(getSession(ip).get, block)
  }

  def handleRequest(ip: String, r: Request): Message = {
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    var n: Point = null
    if (!hasSession(ip)) {
      if (addSession(ip)) {
        val s = getSession(ip).get
        n = s.maze(0)(0).get
      } else return ErrorType.DENIED
    } else {
      val s = getSession(ip).get
      if (!s.maze.robotPosition(r.x, r.y)) return ErrorType.INVALID
      n = s.maze(r.x)(r.y).get
      s.addPoint(r.x, r.y)
      val v = sessions.get(s)
      v.updateSession()
      if (!v.isShown) {
        v.isShown = true
        Interface.addSimTab(v, s.client.ip, ask = false)
      }
    }
    val t = n.asTuple
    Response(t._1, t._2, t._3, t._4)
  }
}
