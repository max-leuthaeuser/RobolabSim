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
import java.net.{URLEncoder, URLDecoder}

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

/** Implicit conversions from [[tud.robolab.model.TokenRequest]] to json.
  *
  * {{{
  *   import TokenRequestProtocol._
  *   val json = TokenRequest(...).toJson
  * }}}
  */
object TokenRequestProtocol extends DefaultJsonProtocol {
  implicit val TokenRequestFormat = jsonFormat1(TokenRequest)
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

/** Implicit conversions from [[tud.robolab.model.TestMessage]] to json.
  *
  * {{{
  *   import TestMessageProtocol._
  *   val json = TestMessage(...).toJson
  * }}}
  */
object TestMessageProtocol extends DefaultJsonProtocol {
  implicit val TestMessageFormat = jsonFormat2(TestMessage)
}

import RequestProtocol._
import QueryResponseProtocol._
import ErrorMessageProtocol._
import TokenRequestProtocol._
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
object MessageJsonProtocol extends DefaultJsonProtocol {

  implicit object MessageJsonFormat extends RootJsonFormat[Message] {
    def write(p: Message) = {
      p match {
        case r: Ok => "Ok".toJson
        case test: TestMessage => test.toJson
        case m: MapRequest => m.toJson
        case t: TokenRequest => t.toJson
        case r: QueryResponse => r.toJson
        case b: ErrorMessage => b.toJson
        case p: PathResponse => JsArray(p.way.map(t =>
          JsObject("point" -> t._1.toJson,
            "properties" -> t._2.toJson)).toList)
        case req: Request => req.toJson
        case _ => deserializationError("Message expected!")
      }
    }

    def read(value: JsValue) = ???
  }

}

/** Defines our service behavior independently from the service actor. */
trait SimulationService extends HttpService {
  private def getUpTime: String = TimeUtils.uptime

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            """<html>
              <body>
                <h1>Welcome to <i>RobolabSim</i></h1>
                <b>Server status page</b><br/>
                <ul>
                  <li><i>Uptime:</i> %s</li>
                  <li><i>Sessions:</i> %s</li>
                  %s
                </ul>
              </body>
            </html>""".format(getUpTime,
                SessionManager.numberOfSessions(),
                SessionManager.getSessionsAsList.map(s => {
                  "<li>%s (<a href=\"/gettest?id=%s\">Test result</a>)</li>".format(s.client.ip, s.client.ip)
                }).mkString("<ul>", "", "</ul>"))
          }
        }
      }
    } ~
      path("query") {
        parameters('id, 'values) {
          (id, values) =>
            (get | put) {
              ctx =>
                val ip = id
                import MessageJsonProtocol._
                val req = values.toString.asJson.convertTo[Request]
                Boot.log.info("Incoming [Query] request from ID [%s]: %s".format(ip, req))
                ctx.complete(SessionManager.handleQueryRequest(ip, req).toJson.compactPrint)
            }
        }
      } ~
      path("history") {
        parameter('id) {
          id =>
            get {
              ctx =>
                val ip = id
                Boot.log.info("Incoming [History] request from ID [%s]".format(ip))

                import MessageJsonProtocol._
                ctx.complete(SessionManager.handleHistoryRequest(ip).toJson.compactPrint)
            }
        }
      } ~
      path("numberOfTokens") {
        parameter('id) {
          id =>
            get {
              ctx =>
                val ip = id
                Boot.log.info("Incoming [NumberOfTokens] request from ID [%s]".format(ip))

                import MessageJsonProtocol._
                ctx.complete(SessionManager.handleNumberOfTokensRequest(ip).toJson.compactPrint)
            }
        }
      } ~
      path("maze") {
        parameters('id, 'values) {
          (id, values) =>
            (get | put) {
              ctx =>
                val ip = id
                import MessageJsonProtocol._
                val req = values.toString.toJson.convertTo[MapRequest]
                Boot.log.info("Incoming [MapChange] request from ID [%s]".format(ip))
                ctx.complete(SessionManager.handleMapRequest(ip, req).toJson.compactPrint)
            }
        }
      } ~
      path("path") {
        parameter('id) {
          id =>
            get {
              ctx =>
                val ip = id
                Boot.log.info("Incoming [Path] request from ID [%s]".format(ip))

                import MessageJsonProtocol._
                ctx.complete(SessionManager.handlePathRequest(ip).toJson.compactPrint)
            }
        }
      } ~
      path("settest") {
        parameter('id, 'values) {
          (id, values) =>
            put {
              ctx =>
                val ip = id
                import MessageJsonProtocol._
                val req = values.toString
                val dec = URLDecoder.decode(req, "UTF-8")
                  .replace("+", " ") // URLEncoder messes up with whitespaces...
                  .replaceAll(System.getProperty("line.separator"), "<br/>") // and we need explicite linebreaks in HTML
                  .asJson.convertTo[TestMessage]

                Boot.log.info("Incoming [Test] put request from ID [%s]".format(ip))

                ctx.complete(SessionManager.handleTestRequest(ip, dec).toJson.compactPrint)
            }
        }
      } ~
      path("gettest") {
        parameter('id) {
          id =>
            get {
              respondWithMediaType(`text/html`) {
                val ip = id
                import MessageJsonProtocol._
                Boot.log.info("Incoming [Test] get request from ID [%s]".format(ip))
                SessionManager.handleTestRequest(ip) match {
                  case t: TestMessage => {
                    complete {
                      """<html>
                          <body>
                            <b>Test result for %s:</b><br/>
                            %s
                          </body>
                         </html>""".format(ip, t.asHtml)
                    }
                  }
                  case m: Message => complete {
                    m.toJson.compactPrint
                  }
                }
              }
            }
        }
      }
}
