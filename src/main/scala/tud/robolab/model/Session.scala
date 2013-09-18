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

case class Client(var ip: String, var blocked: Boolean = false)

case class WayElement(x: Int, y: Int, token: Boolean, time: String)

case class Session(client: Client, var maze: Maze, var way: Seq[WayElement]) {
  def clearWay() {
    way = Seq.empty
  }

  def addPoint(elem: WayElement) {
    way = way :+ elem
  }

  def latestPosition: WayElement = way.last

  override def equals(o: Any) = o match {
    case that: Session => that.client.ip.equals(this.client.ip)
    case _ => false
  }

  override def hashCode = client.ip.hashCode
}
