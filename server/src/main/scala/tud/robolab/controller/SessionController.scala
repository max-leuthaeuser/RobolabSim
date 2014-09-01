/*
 * RobolabSim
 * Copyright (C) 2014  Max Leuthaeuser
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

import tud.robolab.model._
import tud.robolab.view.{Interface, SimulationView}
import tud.robolab.model.{Request, Session}
import tud.robolab.model.Client
import tud.robolab.utils.TimeUtils
import scala.concurrent._
import ExecutionContext.Implicits.global
import tud.robolab.testing.TestRunner

/** Handles incoming requests and sessions.
  *
  * See `handleQueryRequest`, `handlePathRequest` and `handleMapRequest`.
  */
object SessionController
{
  private val sessions = new SessionPool()
  var swing = false

  /**
   * See [[tud.robolab.model.SessionPool]] for doc.
   * @return all sessions stored via the [[tud.robolab.model.SessionPool]].
   */
  def getSessions: SessionPool = sessions

  /**
   * @return all sessions as list stored via the [[tud.robolab.model.SessionPool]].
   */
  def getSessionsAsList: List[Session] = getSessions.all.keys.toList

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
  def set(
    s: Session,
    v: Option[SimulationView]
    )
  {
    sessions.set(s, v)
  }

  /**
   * @param id the group ID
   * @return the [[tud.robolab.model.Session]] with the given IP `ip`.
   */
  def getSession(id: String): Option[Session] = sessions.all.keys.find(_.client.id == id)

  /**
   * @param i the index
   * @return the `i`th [[tud.robolab.model.Session]].
   */
  def getSession(i: Int): Session =
  {
    assert(i >= 0 && i < sessions.all.size)
    sessions.all.keys.toSeq(i)
  }

  /**
   * @param id the group ID
   * @return true if there is a [[tud.robolab.model.Session]] with the given IP `id` is stored, false otherwise.
   */
  def hasSession(id: String): Boolean = sessions.all.keys.exists(_.client.id == id)

  /**
   * @param id id the group ID
   * @return true if the [[tud.robolab.model.Session]] with the given IP `id` is blocked, false otherwise.
   */
  private def sessionBlocked(id: String): Boolean = if (hasSession(id)) getSession(id).get.client.blocked else false

  /**
   * @param id remove the [[tud.robolab.model.Session]] with the given IP `id`.
   */
  def removeSession(id: String)
  {
    if (hasSession(id)) {
      val s = getSession(id).get
      sessions.remove(s)
    }
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to remove.
   */
  def removeSession(s: Session)
  {
    sessions.remove(s)
  }

  /**
   * @param s the [[tud.robolab.model.Session]] to add.
   */
  def addSession(s: Session)
  {
    if (!hasSession(s.client.id)) {
      val v: Option[SimulationView] = swing match {
        case true => Option.empty
        case false => Option(new SimulationView(s, false))
      }
      sessions.set(s, v)
    }
  }

  /**
   * @param id create a nre [[tud.robolab.model.Session]] and add it to the [[tud.robolab.model.SessionPool]].
   */
  def addSession(id: String): Boolean =
  {
    if (!hasSession(id) && !sessionBlocked(id)) {
      val s = Session(Client(id))
      swing match {
        case false =>
          val v = new SimulationView(s)
          if (Interface.addSimTab(v, id)) {
            sessions.set(s, Option(v))
            return true
          }
        case true =>
          sessions.set(s, Option.empty)
          return true
      }
    }
    false
  }

  /**
   * @param id the IP for the the [[tud.robolab.model.Session]] to block or unblock depending on `block`.
   * @param block `true` means block the [[tud.robolab.model.Session]] `s`, false means unblock it.
   */
  def blockSession(
    id: String,
    block: Boolean = true
    )
  {
    if (!hasSession(id)) {
      val s = Session(Client(id))
      val v = new SimulationView(s)
      v.isShown = false
      sessions.set(s, Option(v))
    }
    sessions.block(getSession(id).get, block)
  }

  /**
   * Handle the incoming request, calculating the new robot position
   * and return the appropriate result.
   *
   * @param id the group ID
   * @param r the [[tud.robolab.model.Request]]
   * @return a [[tud.robolab.model.Message]] regarding to the result of this call.
   */
  def handleQueryRequest(
    id: String,
    r: Request
    ): Message =
  {
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    if (!hasSession(id))
      if (!addSession(id))
        return ErrorType.DENIED

    val s = getSession(id).get
    val err = !s.maze.setRobot(Coordinate(r.x, r.y))

    val token = err match {
      case true => false
      case false => s.maze.getPoint(Coordinate(r.x, r.y)).get.token
    }
    val wayElememt = WayElement(r.x, r.y, token, TimeUtils.nowAsString)

    s.addHistoryElement(wayElememt)

    if (err) return ErrorType.INVALID
    else s.addWayElement(wayElememt)

    val n = s.maze.getPoint(Coordinate(r.x, r.y)).get
    val v = sessions.get(s)
    v.foreach(view => {
      view.updateSession()
      if (!view.isShown) {
        view.isShown = true
        Interface.addSimTab(view, s.client.id, ask = false)
      }
    })

    QueryResponseFactory.fromPoint(n)
  }

  /**
   * Handle the incoming request, calculating the full robot path that is known until now
   * and return the appropriate result.
   *
   * @param id the group ID
   * @return a [[tud.robolab.model.Message]] containing the path regarding to the result of this call.
   */
  def handlePathRequest(id: String): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_PATH
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    val s = getSession(id).get
    PathResponse(
      s.path.map(p => {
        s.maze.getPoint(Coordinate(p.x, p.y)) match {
          case Some(point) => (Request(p.x, p.y), QueryResponseFactory.fromPoint(point))
          case None => throw new IllegalArgumentException
        }
      }))
  }

  /**
   * Handle the incoming request, set the new map if possible and return the the appropriate result.
   *
   * @param id the group ID
   * @param r the [[tud.robolab.model.MapRequest]]
   * @return a [[tud.robolab.model.Message]] regarding to the result of this call.
   */
  def handleMapRequest(
    id: String,
    r: MapRequest
    ): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_PATH
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    val s = getSession(id).get
    MapController.changeMap(r.map, s, getView(s)) match {
      case true => Ok()
      case false => ErrorType.NO_MAP
    }
  }

  /**
   * Handle the incoming request, returning the current history if possible.
   *
   * @param id the group ID
   * @return a [[tud.robolab.model.Message]] containing the history regarding to the result of this call.
   */
  def handleHistoryRequest(id: String): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_PATH
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    val s = getSession(id).get
    PathResponse(s.history.map(p => {
      val point = s.maze.isValid(Coordinate(p.x, p.y)) match {
        case true => s.maze.getPoint(Coordinate(p.x, p.y)).get
        case false => Point(Seq.empty)
      }
      (Request(p.x, p.y), QueryResponseFactory.fromPoint(point))
    }))
  }

  /**
   * Handle the incoming request, return the the appropriate test result.
   *
   * @param id the group ID
   * @return a [[tud.robolab.model.Message]] regarding to the result of this call.
   */
  def handleTestRequest(id: String): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_ID
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    val s = getSession(id).get
    val st = s.test.status
    TestMessage(s.test.result, st match {
      case TestResult.SUCCESS => true
      case TestResult.FAILED => false
    })
  }

  def handleRunTestRequest(
    id: String
    ): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_ID
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    future {
      blocking {
        TestRunner.run(id)
      }
    }
    Ok()
  }

  def handleResetRequest(
    id: String
    ): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_ID
    if (sessionBlocked(id)) return ErrorType.BLOCKED

    val s = getSession(id).get
    s.clearHistory()
    s.clearWay()
    s.maze = Maze.default
    s.test = Test()
    Ok()
  }

  def handleRemoveIDRequest(
    id: String
    ): Message =
  {
    if (!hasSession(id)) return ErrorType.NO_ID
    removeSession(id)
    Ok()
  }
}
