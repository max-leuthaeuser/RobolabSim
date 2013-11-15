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
import tud.robolab.utils.TimeUtils
import tud.robolab.controller.MainController

/** Application main entry point. Create and run all relevant actors and bind spray services. */
object Boot extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[SimulationServiceActor], "sim-service")

  // get our logging object
  val log = system.log

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = Config.IP, port = Config.PORT)

  // start the server itself
  MainController(Config.TESTING)

  /** Call this method on terminating the server for unbinding. */
  def terminate() {
    log.info("Terminating server ...")
    IO(Http) ! Http.Unbind
    log.info("Done. All services terminated normally.")
  }
}
