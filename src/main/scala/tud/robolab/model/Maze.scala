package tud.robolab.model

import PointJsonProtocol._
import spray.json._
import Direction._

case class Maze(private val data: Seq[Seq[Option[Point]]], robot: Robot = Robot()) {
  assert(data != null && data(0) != null)
  assert(data != None && data(0) != None)
  robotPosition(robot.x, robot.y)

  def height: Int = data(0).size

  def width: Int = data.size

  def apply(x: Int)(y: Int): Option[Point] = data(x)(y)

  def points: Seq[Seq[Option[Point]]] = data

  def robotPosition(x: Int, y: Int): Boolean = {
    if (x >= width || y >= height || x < 0 || y < 0) return false
    data(robot.x)(robot.y).get.robot = false
    robot.x = x
    robot.y = y
    data(x)(y).get.robot = true
    true
  }
}

object Maze {
  def empty(width: Int, height: Int): Maze = {
    val max_x = width - 1
    val max_y = height - 1
    Maze((0 to max_x).map(x =>
      (0 to max_y).map(y => {
        val p = (x, y) match {
          case (0, 0) => Point(Seq(SOUTH, EAST))
          case (xs, xy) if xs == max_x && xy == max_y => Point(Seq(NORTH, WEST))
          case (xs, xy) if xs == 0 && xy == max_y => Point(Seq(WEST, SOUTH))
          case (xs, xy) if xs == max_x && xy == 0 => Point(Seq(EAST, NORTH))
          case (xs, xy) if xs == max_x => Point(Seq(NORTH, EAST, WEST))
          case (xs, xy) if xy == max_y => Point(Seq(SOUTH, WEST, NORTH))
          case (xs, xy) if xs == 0 => Point(Seq(SOUTH, EAST, WEST))
          case (xs, xy) if xy == 0 => Point(Seq(SOUTH, EAST, NORTH))
          case _ => Point()
        }
        Option(p)
      }).toSeq
    ).toSeq)
  }

  def empty: Maze = empty(6, 6)
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
