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

/**
 * A case class representing a Coordinate in the [[tud.robolab.model.Maze]] as tuple of two Ints.
 *
 * @param x the x coordinate as Int
 * @param y the y coordinate as Int
 */
case class Coordinate(
  x: Int,
  y: Int
  )
{
  override def toString: String = "<html>x: <b>%s</b>, y: <b>%s</b></html>".format(x, y)
}
