package tud.robolab.utils

import tud.robolab.model.Coordinate
import tud.robolab.model.Direction
import tud.robolab.model.Direction._

import scala.collection.mutable

object Relation
{
  def neighbor[V](
    model: mutable.Map[Coordinate, V],
    c: Coordinate,
    dir: Direction
    ): Option[V] =
  {
    dir match {
      case NORTH => model.get(Coordinate(c.x, c.y - 1))
      case EAST => model.get(Coordinate(c.x + 1, c.y))
      case SOUTH => model.get(Coordinate(c.x, c.y + 1))
      case WEST => model.get(Coordinate(c.x - 1, c.y))
    }
  }

  def offset(
    from: Coordinate,
    to: Coordinate
    ) = to match {
    case Coordinate(0, 0) => Coordinate(from.x, -1 * from.y)
    case Coordinate(x, y) if from.x >= 0 && from.y < 0 => Coordinate(from.x - to.x, to.y + from.y)
    case Coordinate(x, y) if from.x < 0 => Coordinate(from.x + to.x, to.y - from.y)
    case _ => Coordinate(from.x - to.x, to.y - from.y)
  }
}