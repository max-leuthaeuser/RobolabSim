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

import Direction._
import spray.json._

case class Point(
  private var data: Seq[Direction] = Direction.values.toSeq,
  var token: Boolean = false,
  var robot: Boolean = false) extends Subject[Point]
{
  assert(data != null)

  private var callback: Option[() => Unit] = Option.empty

  private val hash = java.util.UUID.randomUUID.toString

  def has(dir: Direction): Boolean = data.contains(dir)

  def addCallback(h: () => Unit)
  {
    callback = Option(h)
  }

  def +(
    dir: Direction,
    notify: Boolean = true)
  {
    if (!has(dir)) {
      data = data :+ dir
      callback.foreach(_())
      if (notify)
        notifyObservers()
    }
  }

  def -(
    dir: Direction,
    notify: Boolean = true)
  {
    if (has(dir)) {
      data = data diff Seq(dir)
      callback.foreach(_())
      if (notify)
        notifyObservers()
    }
  }

  def directions: Seq[Direction] = data

  def asTuple: (Boolean, Boolean, Boolean, Boolean, Boolean) = (
    has(NORTH),
    has(EAST),
    has(SOUTH),
    has(WEST),
    token
    )

  override def hashCode(): Int = hash.hashCode

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: Point => this.hashCode() == other.hashCode()
    case _ => false
  }
}

object PointJsonProtocol extends DefaultJsonProtocol
{

  implicit object PointJsonFormat extends RootJsonFormat[Point]
  {
    def write(p: Point) =
    {
      val dirs = p.directions.map(s => JsString(s.toString))
      JsObject("dirs" -> JsArray(dirs.toList), "token" -> JsBoolean(p.token))
    }

    def read(value: JsValue) = value.asJsObject.getFields("dirs", "token") match {
      case Seq(JsArray(dirs), JsBoolean(token)) => Point(
        dirs.map(s => Direction.from(s.toString().replaceAll("\"", ""))), token)
      case _ => deserializationError("Point expected!")
    }
  }

}
