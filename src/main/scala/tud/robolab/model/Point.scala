package tud.robolab.model

import Direction._
import spray.json._

case class Point(private var data: Seq[Direction] = Direction.values.toSeq, var token: Boolean = false) {
  var robot = false

  assert(data != null)
  assert(data != None)

  def has(dir: Direction): Boolean = data.contains(dir)

  def +(dir: Direction) {
    has(dir) match {
      case false => data = data :+ dir
      case _ =>
    }
  }

  def -(dir: Direction) {
    has(dir) match {
      case true => data = data diff Seq(dir)
      case _ =>
    }
  }

  def directions: Seq[Direction] = data

  def asTuple: (Boolean, Boolean, Boolean, Boolean) = (
    has(NORTH),
    has(EAST),
    has(SOUTH),
    has(WEST)
    )
}

object PointJsonProtocol extends DefaultJsonProtocol {

  implicit object PointJsonFormat extends RootJsonFormat[Point] {
    def write(p: Point) = {
      val dirs = p.directions.map(s => JsString(s.toString))
      JsObject("dirs" -> JsArray(dirs.toList), "token" -> JsBoolean(p.token))
    }

    def read(value: JsValue) = value.asJsObject.getFields("dirs", "token") match {
      case Seq(JsArray(dirs), JsBoolean(token)) => Point(dirs.map(s => Direction.from(s.toString().replaceAll("\"", ""))), token)
      case _ => deserializationError("Point expected!")
    }
  }

}
