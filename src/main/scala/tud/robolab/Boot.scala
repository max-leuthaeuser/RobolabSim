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

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import com.typesafe.config.ConfigFactory
import tud.robolab.view.Interface
import tud.robolab.utils.IOUtils

object Boot extends App {
  val conf = ConfigFactory.load("application.conf")
  lazy val IP = conf.getString("spray.can.server.ip")
  lazy val PORT = conf.getInt("spray.can.server.port")

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[SimulationServiceActor], "sim-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = IP, port = PORT)

  Interface.startup(Array.empty)

  def terminate {
    println("[" + IOUtils.now + "] Shutting server down ...")
    IO(Http) ! Http.Unbind
    println("[" + IOUtils.now + "] Done.")
  }
}