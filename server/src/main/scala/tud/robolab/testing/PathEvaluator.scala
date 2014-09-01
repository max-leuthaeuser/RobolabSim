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

import org.jgrapht.alg.DijkstraShortestPath
import scala.collection.JavaConversions._
import scala.collection.mutable

class PathEvaluator(path: Seq[Node])
{
  private var tokenCount = 3

  private def getBuilder = new SimpleGraphBuilder(path).setTokenCount(tokenCount)

  def setTokenCount(tokenCount: Int): PathEvaluator =
  {
    assert(tokenCount >= 0)
    this.tokenCount = tokenCount
    this.getBuilder.setTokenCount(tokenCount)
    this
  }

  def validateOneStepConstraint: Boolean =
  {
    val graph = getBuilder.constructPath

    for (dE <- graph.edgeSet()) {
      val a = graph.getEdgeSource(dE)
      val b = graph.getEdgeTarget(dE)

      // x values of a and b should only differ by max. 1; So I calculate the absolute difference
      val changeX = Math.abs(a.x - b.x)

      // y values of a and b should only differ by max. 1; So I calculate the absolute difference
      val changeY = Math.abs(a.y - b.y)

      // for a correct path, only one changeX or changeY are allowed to be max. 1. Never both of them (would be a diagonal path)
      val changeXY = changeX + changeY

      // changeX > 1 means, that you made a step across 2 vertexes --> not allowed
      // same for changeY
      // changeXY > 1 means, that you made a diagonal step --> not allowed
      if (changeX > 1 || changeY > 1 || changeXY > 1) {
        return false
      }
    }

    true
  }

  def validateOnlyOnLineConstraint: Boolean =
  {
    val graph = getBuilder.constructPath

    for (dE <- graph.edgeSet()) {

      val a = graph.getEdgeSource(dE)
      val b = graph.getEdgeTarget(dE)

      if (a.x + 1 == b.x) {
        // B is in the south of A
        if (!a.south || !b.north) {
          return false
        }
      }
      if (b.x + 1 == a.x) {
        // B is in the north of A
        if (!a.north || !b.south) {
          return false
        }
      }
      if (a.y + 1 == b.y) {
        // B is in the east of A
        if (!a.east || !b.west) {
          return false
        }
      }
      if (b.y + 1 == a.y) {
        // B is in the west of A
        if (!a.west || !b.east) {
          return false
        }
      }
    }

    true
  }

  def validateMaximumVisitedCount: Boolean =
  {
    // a multigraph of the exploration (the path until the last token was found). A multigraph allows multiple edges between two vertexes
    // there is also a pseudo start point added to this. Reason --> see the for loop
    val multigraph = getBuilder.makeMultiGraph.addStartPoint().constructExplorationPhaseGraph

    // the graph representation of the exploration phase
    val graph = getBuilder.constructExplorationPhaseGraph

    for (n <- graph.vertexSet()) {
      // degreeOf() counts the touching edges of a vertex. So in the SimpleGraph this is the equivalent to the neighbors
      val neighbors = graph.degreeOf(n)

      // degreeOf() in the multigraph is the equivalent to all incoming AND outgoing moves from/to this vertex
      // assuming that for every income there is also a outgoing Edge, the degreeOf(n)/2 would be the visitedCount
      // the rounding is necessary for odd degrees. In this case it's equivalent to Math.ceil()
      // for a correct visitedCount of the startNode, there is added a pseudoStartNode from outside the maze, so
      // that this node has a valid amount of touching edges
      val visited = Math.round(multigraph.degreeOf(n) / 2)

      if (visited > neighbors) {
        return false
      }
    }

    true
  }

  def validateShortestPath: Boolean =
  {
    // The path from the Node where the last token was found until the end
    val driveHome = getBuilder.constructDriveHomePhaseGraph
    // The Maze which is known by the students solution, so also unvisited Nodes are contained
    val knownMaze = getBuilder.constructKnownMaze

    // the Node where the last token was found
    val lastToken = getBuilder.getLastTokenNode

    // need the pseudoNodes for the knownMaze --> see tud.robolab.testing.SimpleGraphBuilder.constructKnownMaze
    val pseudoLastToken = new Node(lastToken.x, lastToken.y)

    val pseudoHome = new Node(0, 0)

    //calculate one of the shortest paths
    val shortestPath = new DijkstraShortestPath(knownMaze, pseudoLastToken, pseudoHome)

    //evaluating if the Start Node is included in the shortest path
    var isStartIncluded = false
    for (e <- shortestPath.getPathEdgeList) {
      val source = driveHome.getEdgeSource(e)
      val target = driveHome.getEdgeTarget(e)

      if (source.x == 0 && source.y == 0) {
        isStartIncluded = true
      }

      if (target.x == 0 && target.y == 0) {
        isStartIncluded = true
      }
    }
    
    // only checking the length and if the endNode is the homeNode, because there are more than 1 adequate shortest paths
    (shortestPath.getPathLength == driveHome.edgeSet().size()) && isStartIncluded
  }

  def getSetOfNeighbors(n: Node): Seq[Node] =
  {
    // BE AWARE of these pseudo Nodes -- only for the x & y position, north east west south and token values are fake

    var neighbors: Seq[Node] = Seq[Node]()

    if (n.east) neighbors = neighbors :+ new Node(n.x, n.y + 1)
    if (n.west) neighbors = neighbors :+ new Node(n.x, n.y - 1)
    if (n.north) neighbors = neighbors :+ new Node(n.x - 1, n.y)
    if (n.south) neighbors = neighbors :+ new Node(n.x + 1, n.y)

    neighbors
  }

  def getSetOfNotVisitedNeighbors(
    pathUntil: Seq[Node],
    n: Node): Seq[Node] =
    getSetOfNeighbors(n).filter(v => pathUntil.contains(v)).distinct

  def validateHistory: Int = path count (t => !t.east && !t.west && !t.north && !t.south)

  def validateIfNextNodeIsAlreadyVisited(
    next: Node,
    notVisitedNeighborsOfOrigin: Seq[Node]): Boolean =
  {
    if (notVisitedNeighborsOfOrigin.nonEmpty) {
      val pseudoNodeOfNext = new Node(next.x, next.y)
      if (notVisitedNeighborsOfOrigin.contains(pseudoNodeOfNext)) {
        return true
      }
      return false
    }
    true
  }

  def validateIfThereIsADirectUnknownPathDriveIt: Boolean =
  {
    def eval(revPath: Seq[Node]): Boolean =
    {
      revPath match {
        case hd :: hd2 :: tail => validateIfNextNodeIsAlreadyVisited(hd, getSetOfNotVisitedNeighbors(hd2 :: tail, hd)) && eval(hd2 :: tail)
        case hd :: Nil => true
        case Nil => throw new IllegalArgumentException("The path is empty!")
      }
    }
    eval(path.reverse)
  }

  def foundUniqueTokens: Int = getBuilder.constructPath.vertexSet().filter(_.token).toSet.size

  def validateCompleteMazeIsExplored: Boolean =
  {
    val knownMaze = getBuilder.constructKnownMaze
    val path = getBuilder.constructPath

    val knownSize = knownMaze.vertexSet().size
    val pathSize = path.vertexSet().size

    (knownSize != 0) && (pathSize != 0) && (knownSize == pathSize)
  }

  def validateTerminatedAfterWholeMazeIsExplored: Boolean =
  {
    val knownMaze = getBuilder.constructKnownMaze
    val uniqueVisitedNodes = new mutable.HashSet[Node]

    var knownMazeEqualsDrivenPath = false

    for (n <- path) {
      if (!knownMazeEqualsDrivenPath) {
        uniqueVisitedNodes += n
        if (knownMaze.vertexSet().size() == uniqueVisitedNodes.size) knownMazeEqualsDrivenPath = true
      }
      else {
        return false
      }
    }

    knownMazeEqualsDrivenPath
  }
}