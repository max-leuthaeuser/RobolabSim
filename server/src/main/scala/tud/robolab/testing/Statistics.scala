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

package tud.robolab.testing

/*
 * RobolabSim
 * Copyright (C) 2014 Max Leuthaeuser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

class Statistics(path: Seq[Node])
{
  def distinguishLoc(
    a: Node,
    b: Node
    ): Int =
  {
    if (a.x + 1 == b.x) return 3
    // B is in the north of A
    if (b.x + 1 == a.x) return 1
    // B is in the east of A
    if (a.y + 1 == b.y) return 2
    // B is in the west of A
    if (b.y + 1 == a.y) 4
    0
  }

  def calcTurns(
    a: Int,
    b: Int
    ): Int = math.abs(a - b) match {
    case 3 => 1
    case t: Int => t
  }

  def getTurnsIt: Seq[Int] = path.sliding(2).map {
    case Seq(a, b) => distinguishLoc(a, b)
    case _ => 0
  }.sliding(2).map {
    case Seq(a, b) => calcTurns(a, b)
    case _ => 0
  }.toSeq

  def getTurns: Int = getUTurns + getLTurns

  def getUTurns: Int = getTurnsIt.count(_ == 2)

  def getLTurns: Int = getTurnsIt.count(_ == 1)

  def getVisitedNodes: Int = path.size
}
