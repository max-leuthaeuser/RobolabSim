import akka.actor.ActorSystem
import java.net.URLEncoder
import spray.http.{HttpEntity, HttpResponse}
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.json._
import SprayJsonSupport._
import spray.json.{JsArray, JsValue, RootJsonFormat, DefaultJsonProtocol}
import spray.client.pipelining._
import scala.concurrent._
import scala.concurrent.duration._

case class TokenRequest(numberOfTokens: Int)

case class Node(
  x: Int,
  y: Int,
  north: Boolean = false,
  east: Boolean = false,
  south: Boolean = false,
  west: Boolean = false,
  token: Boolean = false)

case class Path(nodes: Seq[Node])

object PathJsonProtocol extends DefaultJsonProtocol
{

  implicit object PathJsonFormat extends RootJsonFormat[Path]
  {
    def write(p: Path) = ???

    def read(value: JsValue) = value match {
      case s: JsArray =>
        val nodes = s.elements.map(_.asJsObject.getFields("point", "properties") match {
          case Seq(JsObject(m1), JsObject(m2)) =>
            val x = m1("x").convertTo[Int]
            val y = m1("y").convertTo[Int]
            val north = m2("north").convertTo[Boolean]
            val east = m2("east").convertTo[Boolean]
            val south = m2("south").convertTo[Boolean]
            val west = m2("west").convertTo[Boolean]
            val token = m2("token").convertTo[Boolean]
            Node(x, y, north, east, south, west, token)
          case _ => deserializationError("Path expected!")
        })
        Path(nodes)
      case _ => deserializationError("Path expected!")
    }
  }

}

object TokenRequestProtocol extends DefaultJsonProtocol
{
  implicit val TokenRequestFormat = jsonFormat1(TokenRequest)
}

import PathJsonProtocol._
import TokenRequestProtocol._

class RoblabSimClient(
  id: String,
  ip: String,
  port: Int)
{
  private val url = "http://%s:%s".format(ip, port)
  private implicit val system = ActorSystem()

  import system.dispatcher

  private val mapEntityContentTypeToJson: HttpResponse => HttpResponse = response =>
    response.withEntity(HttpEntity(`application/json`, response.entity.data))

  val pipelinePath = sendReceive ~> mapEntityContentTypeToJson ~> unmarshal[Path]
  val pipelineTokens = sendReceive ~> mapEntityContentTypeToJson ~> unmarshal[TokenRequest]
  val pipelinePut = sendReceive

  def getPath: Path =
  {
    val request = Get(url + "/path?id=" + id)
    val response = pipelinePath {
      request
    }

    Await.result(response, 10 second)
  }

  def getHistory: Path =
  {
    val request = Get(url + "/history?id=" + id)
    val response = pipelinePath {
      request
    }

    Await.result(response, 10 second)
  }

  def setMap(name: String)
  {
    val content = "%267B%22map%22%3A%22" + name + "%22%7D"
    val request = Put(url + "/maze?id=" + id + content)
    val response = pipelinePut {
      request
    }

    println(Await.result(response, 10 second))
  }

  def sendTest(result: String)
  {
    val success = !result.toLowerCase.contains("failed")
    val payload = URLEncoder.encode(result, "UTF-8").replace("+", "%2B")
    val values = """{"result":"""" + payload + """","status":""" + success + """}"""
    val content = "&values=" + URLEncoder.encode(values, "UTF-8")
    val request = Put(url + "/settest?id=" + id + content)

    val response = pipelinePut {
      request
    }

    println(Await.result(response, 10 second))
  }

  def getNumberOfTokens: Int =
  {
    val request = Get(url + "/numberOfTokens?id=" + id)
    val response = pipelineTokens {
      request
    }

    Await.result(response, 10 second).numberOfTokens
  }
}
