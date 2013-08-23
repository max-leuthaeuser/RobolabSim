package tud.robolab.controller

import scala.collection.concurrent.TrieMap
import tud.robolab.model._
import tud.robolab.view.{Interface, SimulationView}
import tud.robolab.model.{Request, Session}
import tud.robolab.model.Client

// TODO handle multiple simultaneously connection requests
// TODO allow to add sessions manually
object SessionManager {

  class SessionPool extends Subject[SessionPool] {
    private val peer = TrieMap[Session, SimulationView]()

    def get(s: Session) = peer(s)

    def all = peer

    def set(s: Session, v: SimulationView) {
      peer(s) = v
      notifyObservers()
    }

    def remove(i: Int) {
      val s = peer.keys.toIndexedSeq(i)
      Interface.removeSimTap(get(s))
      remove(s)
    }

    def remove(s: Session) {
      peer.remove(s)
      notifyObservers()
    }

    def block(s: Session, block: Boolean) {
      s.client.blocked = block
      notifyObservers()
    }
  }

  val sessions = new SessionPool()

  private def getSession(ip: String): Option[Session] = sessions.all.keys.find(_.client.ip.equals(ip))

  private def hasSession(ip: String): Boolean = sessions.all.keys.exists(_.client.ip.equals(ip))

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
    return false
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
    if (sessionBlocked(ip)) return ErrorMessage("Session blocked!")

    var n: Point = null
    if (!hasSession(ip)) {
      if (addSession(ip)) {
        val s = getSession(ip).get
        n = s.maze(0)(0).get
      } else return ErrorMessage("Session denied!")
    } else {
      val s = getSession(ip).get
      if (!s.maze.robotPosition(r.x, r.y)) return ErrorMessage("Invalid position!")
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
