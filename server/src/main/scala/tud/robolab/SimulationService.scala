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

import akka.actor.{ActorContext, Actor}
import spray.routing._
import Routes._
import spray.routing.directives.CachingDirectives._
import scala.concurrent.duration.Duration

/** Holding the context actor system and the standard route for our service. */
class SimulationServiceActor extends Actor
                                     with SimulationService
{
  // we don't implement our route structure directly in the service actor because
  // we want to be able to test it independently, without having to spin up an actor

  /** The HttpService trait defines only one abstract member, which
    * connects the services environment to the enclosing actor or test
    */
  def actorRefFactory: ActorContext = context

  /** this actor only runs our route, but you could add
    * other things here, like request stream processing
    * or timeout handling.
    */
  def receive: Receive = runRoute(myRoute)
}

/** Defines our service behavior independently from the service actor. */
trait SimulationService extends HttpService
{
  val CACHE_MAX_CAPACITY = 1000
  val CACHE_DURATION = Duration("30 min")

  val simpleCache = routeCache(maxCapacity = CACHE_MAX_CAPACITY, timeToLive = CACHE_DURATION)

  val myRoute = indexRoute ~
    queryRoute ~
    mazeRoute ~
    pathRoute ~
    resetRoute ~
    runTestRoute ~
    waitTestRoute ~
    getTestRoute ~
    adminRoute ~
    removeIDRoute ~
    hideMazesRoute ~
    path(Rest) {
      // serving static files for bootstrap, cached and gzip compressed
      path =>
        get {
          cache(simpleCache) {
            compressResponse() {
              getFromResource("bootstrap/%s" format path)
            }
          }
        }
    }
}
