package tud.robolab

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import tud.robolab.utils.IOUtils
import tud.robolab.model.{Response, Request}
import spray.json._

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
  implicit val requestFormat = jsonFormat3(Request)
}

object ResponseProtocol extends DefaultJsonProtocol {
  implicit val responseFormat = jsonFormat3(Response)
}

import RequestProtocol._

// this trait defines our service behavior independently from the service actor
trait SimulationService extends HttpService {
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
                println("[" + IOUtils.now + "] Incoming Request... ")
                // TODO visualize

                val req = values.asJson.convertTo[Request]
                // TODO handle request
                // ...
                println("[" + IOUtils.now + "] " + req)
                println("[" + IOUtils.now + "] Completed!")

                // TODO answer with proper Response
                ctx.complete("...")
            }
        }
      }
}