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

object TestRunner
{
  /**
   * Run all tests and collect the resulting string from console output.
   *
   * @param id The id of the current group to test
   */
  def run(id: String)
  {
    val stream = new java.io.ByteArrayOutputStream()
    val test = new RobolabTestSpec(id)
    Console.withOut(stream) {
      test.execute(color = false)
    }
    test.fixture.client.sendTest(stream.toString)
  }
}
