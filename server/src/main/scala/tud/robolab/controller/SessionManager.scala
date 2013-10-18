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
import tud.robolab.utils.TimeUtils

/** Handles incoming requests and sessions.
  *
  * See `handleQueryRequest`, `handlePathRequest` and `handleMapRequest`.
  */
object SessionManager {
  private val sessions = new SessionPool()
  var testing = false

  /** Handling all active sessions.
    * They are basically a tuple (Session -> associated View).
    */
  class SessionPool extends Subject[SessionPool] {
    private val peer = TrieMap[Session, Option[SimulationView]]()

    /**
     * @param s a [[tud.robolab.model.Session]] you want to get the [[tud.robolab.view.SimulationView]] for.
     * @return the [[tud.robolab.view.SimulationView]] associated to that session.
     */
    private[SessionManager] def get(s: Session): Option[SimulationView] = peer(s)

    /**
     * @return all sessions and views.
     */
    private[SessionManager] def all: TrieMap[Session, Option[SimulationView]] = peer

    /**
     * Set the new [[tud.robolab.model.Session]] `s` and its [[tud.robolab.view.SimulationView]] `v`.
     * @param s the new [[tud.robolab.model.Session]] to add
     * @param v the new [[tud.robolab.view.SimulationView]] to add
     */
    private[SessionManager] def set(s: Session, v: Option[SimulationView]) {
      peer(s) = v
      notifyObservers()
    }

    /**
     * @param s the [[tud.robolab.model.Session]] to remove.
     */
    private[SessionManager] def remove(s: Session) {
      get(s).foreach(Interface.removeSimTap)
      peer.remove(s)
      notifyObservers()
    }

    /**
     * @param s the [[tud.robolab.model.Session]] to block or unblock depending on `block`.
     * @param block `true` means block the [[tud.robolab.model.Session]] `s`, false means unblock it.
     */
    private[SessionManager] def block(s: Session, block: Boolean) {
      s.client.blocked = block
      notifyObservers()
    }
  }

  /**
   * See [[tud.robolab.controller.SessionManager.SessionPool]] for doc.
   * @return all sessions stored via the [[tud.robolab.controller.SessionManager.SessionPool]].
   */
  def getSessions: SessionPool = sessions

  /**
   * @return the amount of sessions stored.
   */
  def numberOfSessions(): Int = sessions.all.size

  /**
   * @return if there is at least one [[tud.robolab.model.Session]] stored, false otherwise.
   */
  def hasSessions: Boolean = sessions.all.isEmpty

  /**
   * @param s the [[tud.robolab.model.Session]] you want to get the asociated [[tud.robolab.view.SimulationView]] for.
   * @return the [[tud.robolab.view.SimulationView]] the [[tud.robolab.model.Session]] `s` is associated with.
   */
  def getView(s: Session): Option[SimulationView] = sessions.get(s)

  /**
   * Set the new [[tud.robolab.model.Session]] `s` and its [[tud.robolab.view.SimulationView]] `v`.
   * @param s the new [[tud.robolab.model.Session]] to set
   * @param v the new [[tud.robolab.view.SimulationView]] to set
   */
  def set(s: Session, v: Option[SimulationView]) {
    sessions.set(s, v)
  }

  /**
   * @param ip the IP address
   * @return the [[tud.robolab.model.Session]] with the given IP `ip`.
   */
  def getSession(ip: String): Option[Session] = sessions.all.keys.find(_.client.ip.equals(ip))

  /**
   * @param i the index
   * @return the `i`th [[tud.robolab.model.Session]].
   */
  def getSession(i: Int): Session = {
    assert(i >= 0 && i < sessions.all.size)
    sessions.all.keys.toSeq(i)
  }

  /**
   * @param ip the IP address
   * @return true if there is a [[tud.robolab.model.Session]] with the given IP `ip` is stored, false otherwise.
   */
  def hasSession(ip: String): Boolean = sessions.all.keys.exists(_.client.ip.equals(ip))

  /**
   * @param ip ip the IP address
   * @return true if the [[tud.robolab.model.Session]] with the given IP `ip` is blocked, false otherwise.
   */
  private def sessionBlocked(ip: String): Boolean = if (hasSession(ip)) getSession(ip).get.client.blocked else false

  /**
   * @param ip remove the [[tud.robolab.model.Session]] with the given IP `ip`.
   */
  def removeSession(ip: String) {
    if (hasSession(ip)) {
      val s = getSession(ip).get
      sessions.remove(s)
    }
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to remove.
   */
  def removeSession(s: Session) {
    sessions.remove(s)
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to add.
   */
  def addSession(s: Session) {
    if (!hasSession(s.client.ip)) {
      val v: Option[SimulationView] = testing match {
        case true => Option.empty
        case false => Option(new SimulationView(s, false))
      }
      sessions.set(s, v)
    }
  }

  /**
   * @param ip create a nre [[tud.robolab.model.Session]] and add it to the [[tud.robolab.controller.SessionManager.SessionPool]].
   */
  def addSession(ip: String): Boolean = {
    if (!hasSession(ip) && !sessionBlocked(ip)) {
      val s = Session(Client(ip))      
      testing match {
        case false => {
          val v = new SimulationView(s)
          if (Interface.addSimTab(v, ip)) {
            sessions.set(s, Option(v))
            return true
          }
        }
        case true => {
          sessions.set(s, Option.empty)
          return true
        }
      }
    }
    false
  }

  /**
   * @param ip the IP for the the [[tud.robolab.model.Session]] to block or unblock depending on `block`.
   * @param block `true` means block the [[tud.robolab.model.Session]] `s`, false means unblock it.
   */
  def blockSession(ip: String, block: Boolean = true) {
    if (!hasSession(ip)) {
      val s = Session(Client(ip))
      val v = new SimulationView(s)
      v.isShown = false
      sessions.set(s, Option(v))
    }
    sessions.block(getSession(ip).get, block)
  }

  /**
   * Handle the incoming request, calculating the new robot position
   * and return the appropriate result.
   *
   * @param ip the IP address
   * @param r the [[tud.robolab.model.Request]]
   * @return a [[tud.robolab.model.Message]] regarding to the result of this call.
   */
  def handleQueryRequest(ip: String, r: Request): Message = {
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    if (!hasSession(ip))
      if (!addSession(ip))
        return ErrorType.DENIED

    val s = getSession(ip).get
    var err = false

    if (!s.maze.robotPosition(r.x, r.y)) err = true

    val token = err match {
      case true => false
      case false => s.maze(r.x)(r.y).get.token
    }
    val wayElememt = WayElement(r.x, r.y, token, TimeUtils.now)

    s.addHistoryElement(wayElememt)

    if (err) return ErrorType.INVALID
    else s.addWayElement(wayElememt)

    val n = s.maze(r.x)(r.y).get
    val v = sessions.get(s)
    v.foreach(view => {
      view.updateSession()
      if (!view.isShown) {
        view.isShown = true
        Interface.addSimTab(view, s.client.ip, ask = false)
      }
    })

    QueryResponseFactory.fromPoint(n)
  }

  /**
   * Handle the incoming request, calculating the full robot path that is known until now
   * and return the appropriate result.
   *
   * @param ip the IP address
   * @return a [[tud.robolab.model.Message]] containing the path regarding to the result of this call.
   */
  def handlePathRequest(ip: String): Message = {
    if (!hasSession(ip)) return ErrorType.NO_PATH
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    val s = getSession(ip).get
    PathResponse(
      s.path.map(p => {
        s.maze(p.x)(p.y) match {
          case Some(point) => (Request(p.x, p.y), QueryResponseFactory.fromPoint(point))
          case None => throw new IllegalArgumentException
        }
      }))
  }

  /**
   * Handle the incoming request, set the new map if possible and return the the appropriate result.
   *
   * @param ip the IP address
   * @param r the [[tud.robolab.model.MapRequest]]
   * @return a [[tud.robolab.model.Message]] regarding to the result of this call.
   */
  def handleMapRequest(ip: String, r: MapRequest): Message = {
    if (!hasSession(ip)) return ErrorType.NO_PATH
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    val s = getSession(ip).get
    MainController.changeMap(r.map, s, getView(s)) match {
      case true => Ok()
      case false => ErrorType.NO_MAP
    }
  }

  /**
   * Handle the incoming request, returning the current history if possible.
   *
   * @param ip the IP address
   * @return a [[tud.robolab.model.Message]] containing the history regarding to the result of this call.
   */
  def handleHistoryRequest(ip: String): Message = {
    if (!hasSession(ip)) return ErrorType.NO_PATH
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    val s = getSession(ip).get
    PathResponse(s.history.map(p => {
      val point = s.maze.isValidPosition(p.x, p.y) match {
        case true => s.maze(p.x)(p.y).get
        case false => Point(Seq.empty)
      }
      (Request(p.x, p.y), QueryResponseFactory.fromPoint(point))
    }))
  }

  /**
   * Handle the incoming request, returning the number of tokens of the related session maze if possible.
   *
   * @param ip the IP address
   * @return a [[tud.robolab.model.Message]] containing the number of tokens regarding to the result of this call.
   */
  def handleNumberOfTokensRequest(ip: String): Message = {
    if (!hasSession(ip)) return ErrorType.NO_MAP
    if (sessionBlocked(ip)) return ErrorType.BLOCKED

    val s = getSession(ip).get
    TokenRequest(s.maze.getNumberOfToken)
  }
}
