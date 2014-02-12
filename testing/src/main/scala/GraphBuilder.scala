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

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.UndirectedGraph

trait GraphBuilder[N, E <: DefaultEdge]
{
  /** The known Maze contains all explicit & implicit information, which are known by the solution.
    * It possibly contains nodes, which wasn't visited --> important for evaluating the shortest path back home.
    */
  def constructKnownMaze: UndirectedGraph[N, E]

  /** The path is the representation of the actually driven course */
  def constructPath: UndirectedGraph[N, E]

  /** If you combine these 2 Graphs to one, it will result in the path Graph
    * So these two, are subgraphs of the actual driven path:
    * explorationPhasePath: is the path until all tokens was found
    * driveHomePhasePath: is the Path back home, after the last token was found (empty if there are no tokens in the maze)
    * @see [[GraphBuilder]].constructDriveHomePhaseGraph()
    */
  def constructExplorationPhaseGraph: UndirectedGraph[N, E]

  /** If you combine these 2 Graphs to one, it will result in the path Graph
    * So these two, are subgraphs of the actual driven path:
    * explorationPhasePath: is the path until all tokens was found
    * driveHomePhasePath: is the Path back home, after the last token was found (empty if there are no tokens in the maze)
    * @see [[GraphBuilder]].constructExplorationPhaseGraph()
    */
  def constructDriveHomePhaseGraph: UndirectedGraph[N, E]

  def getLastTokenNode: N

  def setTokenCount(tokenCount: Int): GraphBuilder[N, E]

  def makeMultiGraph: GraphBuilder[N, E]

  def addStartPoint: GraphBuilder[N, E]
}
