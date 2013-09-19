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
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import tud.robolab.model.QueryResponse
import tud.robolab.model.ErrorMessage
import tud.robolab.model.Request

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class SimulationServiceActor extends Actor with SimulationService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

object RequestProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat2(Request)
}

object MapRequestProtocol extends DefaultJsonProtocol {
  implicit val mapRequestFormat = jsonFormat1(MapRequest)
}

object QueryResponseProtocol extends DefaultJsonProtocol {
  implicit val queryResponseFormat = jsonFormat5(QueryResponse)
}

object ErrorMessageProtocol extends DefaultJsonProtocol {
  implicit val ErrorMessageFormat = jsonFormat2(ErrorMessage)
}

import RequestProtocol._
import QueryResponseProtocol._
import ErrorMessageProtocol._
import MapRequestProtocol._

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

// this trait defines our service behavior independently from the service actor
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
                ctx.complete {
                  Future[String] {
                    val r = SessionManager.handleQueryRequest(ip, req).toJson.compactPrint
                    println("[%s] Completed!".format(TimeUtils.now))
                    r
                  }
                }
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
                ctx.complete {
                  Future[String] {
                    val r = SessionManager.handleMapRequest(ip, req).toJson.compactPrint
                    println("[%s] Completed!".format(TimeUtils.now))
                    r
                  }
                }
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
            ctx.complete {
              Future[String] {
                val r = SessionManager.handlePathRequest(ip).toJson.compactPrint
                println("[%s] Completed!".format(TimeUtils.now))
                r
              }
            }
        }
      }
}