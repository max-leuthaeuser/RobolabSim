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

package tud.robolab.utils

import tud.robolab.model.{Direction, Coordinate}
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

  def neighborCoordinate(
    c: Coordinate,
    dir: Direction
    ): Coordinate =
  {
    dir match {
      case NORTH => Coordinate(c.x, c.y - 1)
      case EAST => Coordinate(c.x + 1, c.y)
      case SOUTH => Coordinate(c.x, c.y + 1)
      case WEST => Coordinate(c.x - 1, c.y)
    }
  }

  def neighbors[V](
    model: mutable.Map[Coordinate, V],
    c: Coordinate
    ): Set[V] = Direction.values.map(neighbor(model, c, _)).flatten

  def neighborsCoordinate(
    c: Coordinate
    ): Set[Coordinate] = Direction.values.map(neighborCoordinate(c, _))

  def offset(
    from: Coordinate,
    to: Coordinate
    ): Coordinate = to match {
    case Coordinate(0, 0) => Coordinate(from.x, -1 * from.y)
    case Coordinate(x, y) if from.x >= 0 && from.y < 0 => Coordinate(from.x - to.x, to.y + from.y)
    case Coordinate(x, y) if from.x < 0 => Coordinate(from.x + to.x, to.y - from.y)
    case _ => Coordinate(from.x - to.x, to.y - from.y)
  }
}
