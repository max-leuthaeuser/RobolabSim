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

import tud.robolab.model.TestResult.TestResult

object TestResult extends Enumeration
{
  type TestResult = Value
  val SUCCESS = Value("*** SUCCESS ***")
  val FAILED = Value("*** FAILED ***")
}

object Test
{
  def apply(
    result: String,
    status: Boolean
    ): Test =
  {
    val st = status match {
      case true => TestResult.SUCCESS
      case false => TestResult.FAILED
    }
    Test(result, st)
  }
}

case class Test(
  result: String = "No tests done yet. (Reload this page if necessary!)",
  status: TestResult = TestResult.FAILED
  )
