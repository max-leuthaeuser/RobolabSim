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

package tud.robolab.testing

import tud.robolab.controller.{MapController, SessionController}
import tud.robolab.model.{WayElement, Session, QueryResponseFactory, Test}

class RoblabSimClient(id: String)
{
  private def toPath(
    path: Seq[WayElement],
    s: Session): Path =
  {
    Path(path.map(p => {
      s.maze(p.x)(p.y) match {
        case Some(point) =>
          val r = QueryResponseFactory.fromPoint(point)
          val north = r.north
          val east = r.east
          val south = r.south
          val west = r.west
          val token = r.token
          Node(p.x, p.y, north, east, south, west, token)
        case None => throw new IllegalArgumentException
      }
    }))
  }

  def getPath: Path =
  {
    SessionController.getSession(id) match {
      case Some(s) => toPath(s.path, s)
      case None => throw new IllegalArgumentException("No group ID '" + id + "' found.")
    }
  }

  def getHistory: Path =
  {
    SessionController.getSession(id) match {
      case Some(s) => toPath(s.history, s)
      case None => throw new IllegalArgumentException("No group ID '" + id + "' found.")
    }
  }

  def setMap(name: String)
  {
    SessionController.getSession(id) match {
      case Some(s) => MapController.changeMap(name, s, SessionController.getView(s))
      case None => throw new IllegalArgumentException("No group ID '" + id + "' found.")
    }
  }

  def sendTest(result: String)
  {
    val success = !result.contains("*** FAILED ***")
    SessionController.getSession(id) match {
      case Some(s) => s.test = Test(result.replaceAll("\\+", "<br/>"), success)
      case None => throw new IllegalArgumentException("No group ID '" + id + "' found.")
    }
  }

  def getNumberOfTokens: Int =
  {
    SessionController.getSession(id) match {
      case Some(s) => s.maze.getNumberOfToken
      case None => throw new IllegalArgumentException("No group ID '" + id + "' found.")
    }
  }
}
