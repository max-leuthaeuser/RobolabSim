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

import org.jgrapht.graph.{DefaultEdge, Multigraph, SimpleGraph}
import org.jgrapht.UndirectedGraph
import scala.collection.mutable

class SimpleGraphBuilder(var path: Seq[Node]) extends GraphBuilder[Node, DefaultEdge]
{
  private var tokenCount: Int = 3
  private var asMultigraph: Boolean = false
  private var addPseudoStartPoint: Boolean = false

  def constructDriveHomePhaseGraph: UndirectedGraph[Node, DefaultEdge] =
  {
    assert(path != null)

    var lastNode: Node = null
    val uniqueTokenSet = new mutable.HashSet[Node]()

    val graph: UndirectedGraph[Node, DefaultEdge] = asMultigraph match {
      case true => new Multigraph[Node, DefaultEdge](classOf[DefaultEdge])
      case false => new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    }

    // constructs the drive home path INCLUSIVE the Node of the last token
    path.foreach(n => {
      if (n.token) {
        uniqueTokenSet.add(n)
      }
      if (uniqueTokenSet.size == tokenCount) {
        graph.addVertex(n)
        if (lastNode != null) graph.addEdge(lastNode, n)
        lastNode = n
      }
    })

    graph
  }

  def getLastTokenNode: Node = path.filter(_.token).distinct.last

  def setTokenCount(tokenCount: Int): GraphBuilder[Node, DefaultEdge] =
  {
    this.tokenCount = tokenCount
    this
  }

  def makeMultiGraph: GraphBuilder[Node, DefaultEdge] =
  {
    this.asMultigraph = true
    this
  }

  def addStartPoint(): GraphBuilder[Node, DefaultEdge] =
  {
    this.addPseudoStartPoint = true
    this
  }

  def constructExplorationPhaseGraph: UndirectedGraph[Node, DefaultEdge] =
  {
    assert(path != null)

    var lastNode: Node = null
    val uniqueTokenSet = new mutable.HashSet[Node]()

    val graph: UndirectedGraph[Node, DefaultEdge] = asMultigraph match {
      case true => new Multigraph[Node, DefaultEdge](classOf[DefaultEdge])
      case false => new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    }

    if (addPseudoStartPoint) {
      val pseudoStartPoint = new Node(-1, -1)
      graph.addVertex(pseudoStartPoint)
      lastNode = pseudoStartPoint
    }

    path.foreach(n => {
      graph.addVertex(n)
      if (lastNode != null) graph.addEdge(lastNode, n)
      lastNode = n
      if (n.token) {
        uniqueTokenSet.add(n)
      }
      if (uniqueTokenSet.size == tokenCount) return graph
    })

    graph
  }

  def constructKnownMaze: UndirectedGraph[Node, DefaultEdge] =
  {
    assert(path != null)

    val graph: UndirectedGraph[Node, DefaultEdge] = new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    path.foreach(n => {
      val currentNode = new Node(n.x, n.y)
      graph.addVertex(currentNode)

      if (n.north) {
        val northNode = new Node(n.x, n.y + 1)
        graph.addVertex(northNode)
        graph.addEdge(currentNode, northNode)
      }
      if (n.south) {
        val southNode = new Node(n.x, n.y - 1)
        graph.addVertex(southNode)
        graph.addEdge(currentNode, southNode)
      }
      if (n.east) {
        val eastNode = new Node(n.x + 1, n.y)
        graph.addVertex(eastNode)
        graph.addEdge(currentNode, eastNode)
      }
      if (n.west) {
        val westNode = new Node(n.x - 1, n.y)
        graph.addVertex(westNode)
        graph.addEdge(currentNode, westNode)
      }
    })

    graph
  }

  def constructPath: UndirectedGraph[Node, DefaultEdge] =
  {
    assert(path != null)

    val graph: UndirectedGraph[Node, DefaultEdge] = new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])

    var lastNode: Node = null
    path.foreach(n => {
      graph.addVertex(n)
      if (lastNode != null) graph.addEdge(lastNode, n)
      lastNode = n
    })

    graph
  }
}
