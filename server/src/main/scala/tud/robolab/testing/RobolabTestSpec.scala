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

import org.scalatest.{Matchers, FunSuite, GivenWhenThen}
import scala.language.reflectiveCalls

class RobolabTestSpec(id: String) extends FunSuite
                                          with
                                          GivenWhenThen
                                          with
                                          Matchers
{
  val fixture =
    new
      {
        val client = new RoblabSimClient(id)
        val tokenCount = client.getNumberOfTokens
        val evaluator = new PathEvaluator(client.getPath.nodes).setTokenCount(tokenCount)
        val historyEvaluator = new PathEvaluator(client.getHistory.nodes)
      }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")
  info("---")

  test("No empty solutions are allowed.") {
    fixture.client.getPath.nodes.size should not be 0
  }

  test("No invalid positions are allowed (if failed, you drove out of bounds of the maze, in most cases).") {
    fixture.historyEvaluator.validateHistory should not be false
  }

  test("You have to move step by step and you are not permitted to drive diagonals.") {
    fixture.evaluator.validateOneStepConstraint should not be false
  }

  test("You should only move on lines") {
    fixture.evaluator.validateOnlyOnLineConstraint should not be false
  }

  test("You should not visit a node more often then is has neighbors (only until you found the tokens).") {
    fixture.evaluator.validateMaximumVisitedCount should not be false
  }

  test("Find all tokens if they exist. If there are no tokens, you can not find any of them.") {
    fixture.evaluator.foundUniqueTokens shouldEqual fixture.tokenCount
  }

  if (fixture.tokenCount == 0) {
    test("If no token exists you should explore the complete maze.") {
      fixture.evaluator.validateCompleteMazeIsExplored should not be false
    }

    test("If no token exists your algorithm should terminate after the entire maze is explored.") {
      fixture.evaluator.validateTerminatedAfterWholeMazeIsExplored should not be false
    }

    test(
      "When you explore the maze, you should not head back a known path if there are unknown ones directly available.")
    {
      fixture.evaluator.validateIfThereIsADirectUnknownPathDriveIt should not be false
    }
  }

  if (fixture.tokenCount != 0) {
    test(
      "If there are tokens, the robot have to find the shortest path between two nodes, for driving back to start position on an optimal path.")
    {
      fixture.evaluator.validateShortestPath should not be false
    }
  }

  info("---")
  info("Here are some statistics of your course:")
  val stats = new Statistics(fixture.client.getPath.nodes)
  val magicVisitedNodesNumber = 10
  val magicTurnsNumber = 6
  val turns = stats.getTurns
  val Lturns = stats.getLTurns
  val Uturns = stats.getUTurns
  val nodes = stats.getVisitedNodes
  info("You visited %s nodes".format(nodes))
  info("You made %s turns".format(turns))
  info("%s of them are 90 degree turns".format(Lturns))
  info("%s of them are 180 degree turns".format(Uturns))
  val score = (nodes * magicVisitedNodesNumber) + (turns * magicTurnsNumber)
  info("Your score is (the lower the better): " + score)
}
