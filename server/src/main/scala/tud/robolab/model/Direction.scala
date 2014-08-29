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

package tud.robolab.model

/** Enumeration that holds all available directions in a maze and offers
  * some convenience methods.
  */
object Direction extends Enumeration
{
  type Direction = Value
  val NORTH, EAST, SOUTH, WEST = Value

  def oppositeOf(dir: Direction): Direction = dir match {
    case NORTH => SOUTH
    case EAST => WEST
    case SOUTH => NORTH
    case WEST => EAST
    case _ => throw new IllegalArgumentException
  }

  /**
   * @throws an IllegalArgumentException if the param `dir` is no valid known direction
   * @param dir the direction as String
   * @return a [[tud.robolab.model.Direction.Direction]] in dependence of the parameter `dir`
   */
  def from(dir: String): Direction = dir match {
    case "NORTH" => NORTH
    case "EAST" => EAST
    case "SOUTH" => SOUTH
    case "WEST" => WEST
    case c => throw new IllegalArgumentException("No valid direction: " + c)
  }
}
