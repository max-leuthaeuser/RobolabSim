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

package tud.robolab

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import tud.robolab.utils.TimeUtils
import tud.robolab.model._
import spray.json._
import tud.robolab.controller.SessionManager
import tud.robolab.model.QueryResponse
import tud.robolab.model.ErrorMessage
import tud.robolab.model.Request

/** Holding the context actor system and the standard route for our service. */
class SimulationServiceActor extends Actor with SimulationService {
  // we don't implement our route structure directly in the service actor because
  // we want to be able to test it independently, without having to spin up an actor

  /** The HttpService trait defines only one abstract member, which
    * connects the services environment to the enclosing actor or test
    */
  def actorRefFactory = context

  /** this actor only runs our route, but you could add
    * other things here, like request stream processing
    * or timeout handling.
    */
  def receive = runRoute(myRoute)
}

/** Implicit conversions from [[tud.robolab.model.Request]] to json.
  *
  * {{{
  *   import RequestProtocol._
  *   val json = Request(...).toJson
  * }}}
  */
object RequestProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat2(Request)
}

/** Implicit conversions from [[tud.robolab.model.MapRequest]] to json.
  *
  * {{{
  *   import MapRequestProtocol._
  *   val json = MapRequest(...).toJson
  * }}}
  */
object MapRequestProtocol extends DefaultJsonProtocol {
  implicit val mapRequestFormat = jsonFormat1(MapRequest)
}

/** Implicit conversions from [[tud.robolab.model.QueryResponse]] to json.
  *
  * {{{
  *   import QueryResponseProtocol._
  *   val json = QueryResponse(...).toJson
  * }}}
  */
object QueryResponseProtocol extends DefaultJsonProtocol {
  implicit val queryResponseFormat = jsonFormat5(QueryResponse)
}

/** Implicit conversions from [[tud.robolab.model.ErrorMessage]] to json.
  *
  * {{{
  *   import ErrorMessageProtocol._
  *   val json = ErrorMessage(...).toJson
  * }}}
  */
object ErrorMessageProtocol extends DefaultJsonProtocol {
  implicit val ErrorMessageFormat = jsonFormat2(ErrorMessage)
}

import RequestProtocol._
import QueryResponseProtocol._
import ErrorMessageProtocol._
import MapRequestProtocol._

/** Implicit conversions from [[tud.robolab.model.Message]] to json.
  *
  * {{{
  *   import MessageJsonProtocol._
  *   val ok = Ok(...).toJson
  *   val query = QueryResponse(...).toJson
  *   val error = ErrorMessage(...).toJson
  *   val path = PathResponse(...).toJson
  * }}}
  */
object MessageJsonProtocol extends DefaultJsonProtocol {

  implicit object MessageJsonFormat extends RootJsonFormat[Message] {
    def write(p: Message) = {
      p match {
        case r: Ok => "Ok".toJson
        case r: QueryResponse => r.toJson
        case b: ErrorMessage => b.toJson
        case p: PathResponse => JsArray(p.way.map(t =>
          JsObject("point" -> t._1.toJson,
            "properties" -> t._2.toJson)).toList)
        case _ => throw new NotImplementedError()
      }
    }

    def read(value: JsValue) = {
      throw new NotImplementedError()
    }
  }

}

/** Defines our service behavior independently from the service actor. */
trait SimulationService extends HttpService {
  private def getIP(req: HttpRequest) = req.headers.filter(_.name.equals("Remote-Address"))(0).value

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Welcome to
                  <i>RobolabSim</i>
                </h1>
                See the GUI provided to watch your robot navigating around.
              </body>
            </html>
          }
        }
      }
    } ~
      path("query") {
        parameter("") {
          values =>
            (get | put) {
              ctx =>
                val ip = getIP(ctx.request)
                val req = values.asJson.convertTo[Request]

                println("[%s] Incoming Request...".format(TimeUtils.now))
                println("[%s] from [%s] %s".format(TimeUtils.now, ip, req))

                import MessageJsonProtocol._
                ctx.complete(SessionManager.handleQueryRequest(ip, req).toJson.compactPrint)
            }
        }
      } ~
      path("maze") {
        parameter("") {
          values =>
            (get | put) {
              ctx =>
                val ip = getIP(ctx.request)
                val req = values.asJson.convertTo[MapRequest]

                println("[%s] Incoming Maze request...".format(TimeUtils.now))
                println("[%s] from [%s] %s".format(TimeUtils.now, ip, req))

                import MessageJsonProtocol._
                ctx.complete(SessionManager.handleMapRequest(ip, req).toJson.compactPrint)
            }
        }
      } ~
      path("path") {
        get {
          ctx =>
            val ip = getIP(ctx.request)

            println("[%s] Incoming Path request...".format(TimeUtils.now))
            println("[%s] from [%s]".format(TimeUtils.now, ip))

            import MessageJsonProtocol._
            ctx.complete(SessionManager.handlePathRequest(ip).toJson.compactPrint)
        }
      }
}
