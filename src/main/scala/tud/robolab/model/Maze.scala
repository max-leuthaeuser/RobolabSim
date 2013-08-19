package tud.robolab.model

import spray.json._
import PointJsonProtocol._

case class Maze(private val data: Seq[Seq[Option[Point]]]) {
  assert(data != null && data(0) != null)
  assert(data != None && data(0) != None)

  def width: Int = data(0).size

  def height: Int = data.size

  def apply(x: Int)(y: Int): Option[Point] = data(x)(y)

  def points: Seq[Seq[Option[Point]]] = data
}

object MazeJsonProtocol extends DefaultJsonProtocol {

  implicit object MazeJsonFormat extends RootJsonFormat[Maze] {
    def write(p: Maze) = {
      val points: List[List[JsValue]] = p.points.map(ys => {
        ys.map(_ match {
          case None => JsString("None")
          case e => e.get.toJson
        }).toList
      }).toList

      JsArray(points.map(JsArray(_)))
    }

    def read(value: JsValue) = value match {
      case s: JsArray => {
        val points = s.elements.map(_ match {
          case l: JsArray => l.elements.map(_ match {
            case e if e.compactPrint.contains("None") => Option.empty
            case e => Option(e.convertTo[Point])
          }).toSeq
          case _ => throw new IllegalArgumentException
        }).toSeq

        Maze(points)
      }
      case _ => throw new IllegalArgumentException("Maze expected!")
    }
  }

}
