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

package tud.robolab.model

case class Client(
  var ip: String,
  var blocked: Boolean = false)

case class WayElement(
  x: Int,
  y: Int,
  token: Boolean,
  time: String)
{
  def toHtml: String =
  {
    val t = token match {
      case true => " (T) "
      case false => " "
    }

    "x: <b>" + x + "</b> y: <b>" + y + "</b>" + t + "(at " + time + ")"
  }
}

case class Session(
  client: Client,
  var maze: Maze = Maze.default,
  var path: Seq[WayElement] = Seq.empty,
  var history: Seq[WayElement] = Seq.empty,
  var test: Test = Test())
{
  def clearWay()
  {
    path = Seq.empty
  }

  def clearHistory()
  {
    history = Seq.empty
  }

  def addWayElement(elem: WayElement)
  {
    path = path :+ elem
  }

  def addHistoryElement(elem: WayElement)
  {
    history = history :+ elem
  }

  def latestPosition: WayElement = path.last

  def pathToHtml: String = path.isEmpty match {
    case true => "<i>*** No path was driven yet ***</i><br/><br/>"
    case false => path.map("<li>" + _.toHtml + "</li>").mkString("<ul>", "", "</ul>")
  }

  override def equals(o: Any) = o match {
    case that: Session => that.client.ip.equals(this.client.ip)
    case _ => false
  }

  override def hashCode = client.ip.hashCode
}
