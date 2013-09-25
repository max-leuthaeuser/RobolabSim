import akka.actor.ActorSystem
import spray.http.{HttpEntity, HttpResponse}
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.json._
import SprayJsonSupport._
import spray.json.{JsArray, JsValue, RootJsonFormat, DefaultJsonProtocol}
import spray.client.pipelining._
import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.duration._

case class Node(x:Int, y:Int, north: Boolean, east: Boolean, south: Boolean, west: Boolean, token: Boolean)

case class Path(nodes: Seq[Node])

object PathJsonProtocol extends DefaultJsonProtocol {

  implicit object PathJsonFormat extends RootJsonFormat[Path] {
    def write(p: Path) = ???

    def read(value: JsValue) = value match {
      case s: JsArray => {
        val nodes = s.elements.map(_.asJsObject.getFields("point", "properties") match {
          case Seq(JsObject(m1), JsObject(m2)) => {
            val x = m1("x").convertTo[Int]
            val y = m1("y").convertTo[Int]
            val north = m2("north").convertTo[Boolean]
            val east = m2("east").convertTo[Boolean]
            val south = m2("south").convertTo[Boolean]
            val west = m2("west").convertTo[Boolean]
            val token = m2("token").convertTo[Boolean]
	    Node(x, y, north, east, south, west, token)
          }
          case _ => deserializationError("Path expected!")
        })
        Path(nodes)
      }
      case _ => deserializationError("Path expected!")
    }
  }

}

class RoblabSimClient(ip: String, port: Int) {
  private val url = "http://%s:%s".format(ip, port)
  private implicit val system = ActorSystem()

  import system.dispatcher
  import PathJsonProtocol._

  private val mapEntityContentTypeToJson: HttpResponse => HttpResponse = response =>
    response.withEntity(HttpEntity(`application/json`, response.entity.buffer))

  val pipelinePath = sendReceive ~> mapEntityContentTypeToJson ~> unmarshal[Path]
  val pipelineMap = sendReceive

  def getPath(): Path = {
    val request = Get(url + "/path")
    val response = pipelinePath { request }

    Await.result(response, 10 second)	
  }

  def setMap(name: String) {
    val content = "%7B%22map%22%3A%22" + name + "%22%7D"
    val request = Put(url + "/maze?=" + content)
    val response = pipelineMap { request }

    println(Await.result(response, 10 second))
  }
}
