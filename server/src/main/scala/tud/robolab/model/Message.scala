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

sealed abstract class Message

case class Ok() extends Message

case class Request(
  x: Int,
  y: Int) extends Message

case class MapRequest(map: String) extends Message

case class TokenRequest(numberOfTokens: Int) extends Message

case class QueryResponse(
  north: Boolean,
  east: Boolean,
  south: Boolean,
  west: Boolean,
  token: Boolean) extends Message

object QueryResponseFactory
{
  def fromPoint(p: Point) =
  {
    val t = p.asTuple
    QueryResponse(t._1, t._2, t._3, t._4, t._5)
  }
}

case class PathResponse(way: Seq[(Request, QueryResponse)]) extends Message

case class TestMessage(
  result: String,
  status: Boolean) extends Message
{
  def asHtml =
  {
    val r = status match {
      case true => "<i>*** SUCCESS ***</i>"
      case false => "<i>*** FAILED ***</i>"
    }
    r + "<br/><br/><b>Reason:</b><br/>" + result
  }
}

case class ErrorMessage(
  code: Int,
  message: String) extends Message

object ErrorType
{
  val DENIED = ErrorMessage(1, "Connection denied!")
  val BLOCKED = ErrorMessage(2, "Connection blocked!")
  val INVALID = ErrorMessage(3, "Invalid position!")
  val NO_PATH = ErrorMessage(4, "No path found!")
  val NO_MAP = ErrorMessage(5, "No map found!")
  val NO_ID = ErrorMessage(6, "No ID found!")
}