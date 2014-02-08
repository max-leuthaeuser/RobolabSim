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
import tud.robolab.controller.{MainController, SessionManager}
import java.net.{URLEncoder, URLDecoder}

object Routes
{
  private def getUpTime: String = TimeUtils.uptime

  val indexRoute = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          val uptime = TimeUtils.uptime
          val sessions = SessionManager.numberOfSessions()
          val mazeNameRequests = MainController.mazePool.mazeNames.sortWith(_.toLowerCase < _.toLowerCase)
            .map(n => (n, URLEncoder.encode( """{"map":"""" + n + """"}""", "UTF-8")))
          val ids = SessionManager.getSessionsAsList.map(_.client.ip).sortWith(_.toLowerCase < _.toLowerCase)

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
            ctx.complete(SessionManager.handleQueryRequest(ip, req).toJson.compactPrint)
        }
    }
  }

  val historyRoute = path("history") {
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
  }

  val numberOfTokensRoute = path("numberOfTokens") {
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
            SessionManager.handleMapRequest(ip, req)
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
            ctx.complete(SessionManager.handlePathRequest(ip).toJson.compactPrint)
        }
    }
  }

  val setTestRoute = path("settest") {
    parameters('id, 'values) {
      (
        id,
        values) =>
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
  }

  val runTestRoute = path("runtest") {
    parameter('id) {
      id
      =>
        (get | put) {
          ctx =>
            val ip = id
            Boot.log.info("Incoming [Test] run request from ID [%s]".format(ip))
            SessionManager.handleRunTestRequest(ip)
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
            import MessageJsonProtocol._
            Boot.log.info("Incoming [Reset] request from ID [%s]".format(ip))
            SessionManager.handleResetRequest(ip)
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
            SessionManager.handleTestRequest(ip) match {
              case t: TestMessage =>
                val s = SessionManager.getSession(ip).get
                val path = s.path
                val maze = s.maze.asHtml
                complete {
                  tud.robolab.html.testresult(maze, path, ip, t)
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
