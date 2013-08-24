/*
 * RobolabSim
 * Copyright (C) 2013  Max Leuthaeuser
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

object Direction extends Enumeration {
  type Direction = Value
  val NORTH, EAST, SOUTH, WEST = Value

  def from(dir: String): Direction = dir match {
    case "NORTH" => NORTH
    case "EAST" => EAST
    case "SOUTH" => SOUTH
    case "WEST" => WEST
    case c => throw new IllegalArgumentException("" + c)
  }
}
