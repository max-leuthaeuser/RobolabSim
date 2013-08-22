package tud.robolab.controller

import scala.collection.concurrent.TrieMap
import tud.robolab.model._
import tud.robolab.view.{Interface, SimulationView}
import tud.robolab.model.{Request, Session}
import tud.robolab.model.Client

object SessionManager {
  private val sessions = TrieMap[Session, SimulationView]()

  private def getSession(ip: String): Option[Session] = sessions.keys.find(_.client.ip.equals(ip))

  private def hasSession(ip: String): Boolean = sessions.keys.find(_.client.ip.equals(ip)).isDefined

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

  def addSession(ip: String) {
    if (!hasSession(ip) && !sessionBlocked(ip)) {
      val s = Session(Client(ip), Maze.empty, TrieMap.empty)
      val v = new SimulationView(s)
      sessions(s) = v

      Interface.addSimTab(v, ip)
    }
  }

  def blockSession(ip: String, block: Boolean = true) {
    if (hasSession(ip)) getSession(ip).get.client.blocked = block
  }

  def handleRequest(ip: String, r: Request): Message = {
    if (sessionBlocked(ip)) return ErrorMessage("Session blocked!")

    var n: Point = null
    if (!hasSession(ip)) {
      addSession(ip)
      val s = getSession(ip).get
      s.addPoint(0, 0)
      n = s.maze(0)(0).get
    } else {
      val s = getSession(ip).get
      if (!s.maze.robotPosition(r.x, r.y)) return ErrorMessage("Invalid position!")
      n = s.maze(r.x)(r.y).get
      s.addPoint(r.x, r.y)
      sessions(s).updateSession()
    }
    val t = n.asTuple
    Response(t._1, t._2, t._3, t._4)
  }
}
