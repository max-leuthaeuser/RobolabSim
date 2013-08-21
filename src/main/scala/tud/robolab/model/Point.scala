package tud.robolab.model

import Direction._
import spray.json._

case class Point(private var data: Seq[Direction] = Direction.values.toSeq) {
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
}

object PointJsonProtocol extends DefaultJsonProtocol {

  implicit object PointJsonFormat extends RootJsonFormat[Point] {
    def write(p: Point) = {
      val dirs = p.directions.map(s => JsString(s.toString))
      JsArray(dirs.toList)
    }

    def read(value: JsValue) = value match {
      case s: JsArray => {
        val dirs = s.elements.map(s => Direction.from(s.toString().replaceAll("\"", "")))
        Point(dirs)
      }
      case _ => throw new IllegalArgumentException("Point expected!")
    }
  }

}
