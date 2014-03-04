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

import spray.routing.Directives._
import spray.http._
import MediaTypes._
import tud.robolab.utils.TimeUtils
import tud.robolab.model._
import JsonProtocols._
import spray.json._
import spray.httpx.TwirlSupport._
import RequestProtocol._
import MapRequestProtocol._
import TestMessageProtocol._
import tud.robolab.controller.{AuthController, MapController, SessionController}
import java.net.{URLEncoder, URLDecoder}
import scala.concurrent.ExecutionContext
import spray.routing.authentication.BasicAuth
import ExecutionContext.Implicits.global

object Routes
{
  val indexRoute = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          val uptime = TimeUtils.uptime
          val sessions = SessionController.numberOfSessions()
          val mazeNameRequests = MapController.mazePool.mazeNames.sortWith(_.toLowerCase < _.toLowerCase)
            .map(n => (n, URLEncoder.encode( """{"map":"""" + n + """"}""", "UTF-8")))
          val ids = SessionController.getSessionsAsList.map(_.client.id).sortWith(_.toLowerCase < _.toLowerCase)

          tud.robolab.html.index(uptime, sessions, mazeNameRequests, ids)
        }
      }
    }
  }

  val queryRoute = path("query") {
    parameters('id, 'values) {
      (
        id,
        values) =>
        (get | put) {
          ctx =>
            val ip = id
            import MessageJsonProtocol._
            val req = values.toString.asJson.convertTo[Request]

            Boot.log.info("Incoming [Query] request from ID [%s]: %s".format(ip, req))
            ctx.complete(SessionController.handleQueryRequest(ip, req).toJson.compactPrint)
        }
    }
  }

  val mazeRoute = path("maze") {
    parameters('id, 'values) {
      (
        id,
        values) =>
        (get | put) {
          ctx =>
            val ip = id
            import MessageJsonProtocol._
            val req = values.toString.asJson.convertTo[MapRequest]

            Boot.log.info("Incoming [MapChange] request from ID [%s]".format(ip))
            SessionController.handleMapRequest(ip, req)
            ctx.redirect("/gettest?id=" + ip, StatusCodes.Found)
        }
    }
  }

  val pathRoute = path("path") {
    parameter('id) {
      id =>
        get {
          ctx =>
            val ip = id
            Boot.log.info("Incoming [Path] request from ID [%s]".format(ip))

            import MessageJsonProtocol._
            ctx.complete(SessionController.handlePathRequest(ip).toJson.compactPrint)
        }
    }
  }

  val runTestRoute = path("runtest") {
    parameter('id) {
      id
      =>
        (get | put) {
          ctx =>
            val ip = id
            Boot.log.info("Incoming [Test] run request from ID [%s]".format(ip))
            SessionController.handleRunTestRequest(ip)
            ctx.redirect("/waittest?id=" + ip, StatusCodes.Found)
        }
    }
  }

  val waitTestRoute = path("waittest") {
    parameter('id) {
      id
      =>
        get {
          respondWithMediaType(`text/html`) {
            complete {
              tud.robolab.html.waittest(id)
            }
          }
        }
    }
  }

  val resetRoute = path("reset") {
    parameter('id) {
      id
      =>
        (get | put) {
          ctx =>
            val ip = id
            Boot.log.info("Incoming [Reset] request from ID [%s]".format(ip))
            SessionController.handleResetRequest(ip)
            ctx.redirect("/", StatusCodes.Found)
        }
    }
  }

  val getTestRoute = path("gettest") {
    parameter('id) {
      id =>
        get {
          respondWithMediaType(`text/html`) {
            val ip = id
            import MessageJsonProtocol._
            Boot.log.info("Incoming [Test] get request from ID [%s]".format(ip))
            SessionController.handleTestRequest(ip) match {
              case t: TestMessage =>
                val s = SessionController.getSession(ip).get
                val path = s.path
                val maze = MapController.hideMazes match {
                  case false => s.maze.asHtml
                  case true => "Maze hidden"
                }

                if (t.result.contains("No tests done yet")) {
                  complete(tud.robolab.html.testresult(maze, path, ip, testResult = false, t.result, Seq.empty, ""))
                } else {
                  val testcontent = t.result.split("---")
                  val (head, tail) = (testcontent(0), testcontent(2))
                  val body = testcontent(1).split("-").filterNot(e => e.trim.equals("<br/>") || e.trim.isEmpty)
                    .map(s => {
                    (s.contains("*** FAILED ***"), s.replaceAll("\\*\\*\\* FAILED \\*\\*\\*", ""))
                  })

                  complete(tud.robolab.html.testresult(maze, path, ip, t.status, head, body, tail))
                }
              case m: Message => complete {
                m.toJson.compactPrint
              }
            }
          }
        }
    }
  }

  val removeIDRoute = path("remove") {
    authenticate(BasicAuth(AuthController.userPassAuthenticator _, realm = "admin")) {
      auth =>
        parameter('id) {
          id
          =>
            (get | put) {
              ctx =>
                val ip = id
                Boot.log.info("Incoming [Remove ID] request from ID [%s]".format(ip))
                SessionController.handleRemoveIDRequest(ip)
                ctx.redirect("/admin", StatusCodes.Found)
            }
        }
    }
  }

  val hideMazesRoute = path("hideMazes") {
    authenticate(BasicAuth(AuthController.userPassAuthenticator _, realm = "admin")) {
      auth =>
        (get | put) {
          ctx =>
            Boot.log.info("Incoming [Hide Maze] request")
            MapController.hideMazes = !MapController.hideMazes
            ctx.redirect("/admin", StatusCodes.Found)
        }
    }
  }

  val adminRoute = path("admin") {
    authenticate(BasicAuth(AuthController.userPassAuthenticator _, realm = "admin")) {
      auth =>
        get {
          complete {
            val sessions = SessionController.numberOfSessions()
            val ids = SessionController.getSessionsAsList.map(_.client.id).sortWith(_.toLowerCase < _.toLowerCase)

            tud.robolab.html.admin(sessions, ids, MapController.hideMazes)
          }
        }
    }
  }
}
