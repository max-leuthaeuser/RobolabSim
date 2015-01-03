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

package tud.robolab

import spray.json._
import tud.robolab.model._

/**
 * Contains json conversion objects for implicit conversion json <-> some request.
 */
object JsonProtocols
{

  /** Implicit conversions from [[tud.robolab.model.Request]] to json.
    *
    * {{{
    *   import RequestProtocol._
    *   val json = Request(...).toJson
    * }}}
    */
  object RequestProtocol extends DefaultJsonProtocol
  {
    implicit val requestFormat = jsonFormat2(Request)
  }

  /** Implicit conversions from [[tud.robolab.model.MapRequest]] to json.
    *
    * {{{
    *   import MapRequestProtocol._
    *   val json = MapRequest(...).toJson
    * }}}
    */
  object MapRequestProtocol extends DefaultJsonProtocol
  {
    implicit val mapRequestFormat = jsonFormat1(MapRequest)
  }

  /** Implicit conversions from [[tud.robolab.model.QueryResponse]] to json.
    *
    * {{{
    *   import QueryResponseProtocol._
    *   val json = QueryResponse(...).toJson
    * }}}
    */
  object QueryResponseProtocol extends DefaultJsonProtocol
  {
    implicit val queryResponseFormat = jsonFormat5(QueryResponse)
  }

  /** Implicit conversions from [[tud.robolab.model.ErrorMessage]] to json.
    *
    * {{{
    *   import ErrorMessageProtocol._
    *   val json = ErrorMessage(...).toJson
    * }}}
    */
  object ErrorMessageProtocol extends DefaultJsonProtocol
  {
    implicit val ErrorMessageFormat = jsonFormat2(ErrorMessage)
  }

  /** Implicit conversions from [[tud.robolab.model.TestMessage]] to json.
    *
    * {{{
    *   import TestMessageProtocol._
    *   val json = TestMessage(...).toJson
    * }}}
    */
  object TestMessageProtocol extends DefaultJsonProtocol
  {
    implicit val TestMessageFormat = jsonFormat2(TestMessage)
  }

  import RequestProtocol._
  import QueryResponseProtocol._
  import ErrorMessageProtocol._
  import MapRequestProtocol._
  import TestMessageProtocol._

  /** Implicit conversions from [[tud.robolab.model.Message]] to json.
    *
    * {{{
    *   import MessageJsonProtocol._
    *   val ok = Ok(...).toJson
    *   val query = QueryResponse(...).toJson
    *   val error = ErrorMessage(...).toJson
    *   val path = PathResponse(...).toJson
    *   // ...
    * }}}
    */
  object MessageJsonProtocol extends DefaultJsonProtocol
  {

    implicit object MessageJsonFormat extends RootJsonFormat[Message]
    {
      def write(p: Message): JsValue =
      {
        p match {
          case r: Ok => "Ok".toJson
          case test: TestMessage => test.toJson
          case m: MapRequest => m.toJson
          case r: QueryResponse => r.toJson
          case b: ErrorMessage => b.toJson
          case p: PathResponse => JsArray(p.way.toVector.map(t =>
            JsObject("point" -> t._1.toJson,
              "properties" -> t._2.toJson)))
          case req: Request => req.toJson
          case _ => deserializationError("Message expected!")
        }
      }

      override def read(json: JsValue): Message = ???
    }

  }

}
