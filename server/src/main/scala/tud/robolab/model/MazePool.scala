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

import java.util.Observable

import spray.json._
import tud.robolab.model.MazeJsonProtocol._
import tud.robolab.utils.IOUtils
import java.io.File
import scala.collection.concurrent.TrieMap

object MazePool
{
  val STD_MAPS_FOLDER = "maps/"
  val STD_MAPS_SUFFIX = ".maze"
}

/** Holds all available mazes with their name. */
class MazePool extends Observable
{
  val pool = TrieMap[String, Maze]()

  IOUtils.createDirectory(new File(MazePool.STD_MAPS_FOLDER))
  IOUtils.getFileTreeFilter(new File(MazePool.STD_MAPS_FOLDER), MazePool.STD_MAPS_SUFFIX).foreach(m => {
    pool(m) = IOUtils.readFromFile(new File(MazePool.STD_MAPS_FOLDER + m + MazePool.STD_MAPS_SUFFIX)).parseJson
      .convertTo[Maze]
  })

  /**
   * @return all available maze names
   */
  def mazeNames: Seq[String] = pool.keys.toSeq

  /**
   * @param name the name of the maze
   * @return the [[tud.robolab.model.Maze]] that is stored under the given `name`
   */
  def apply(name: String): Maze = pool(name)

  /** Add a new [[tud.robolab.model.Maze]] to this pool.
    *
    * @param name the desired name of the maze
    * @param maze an instance of [[tud.robolab.model.Maze]]
    */
  def addMaze(
    name: String,
    maze: Maze
    )
  {
    pool(name) = maze
    setChanged()
    notifyObservers()
  }

  /** Remove a [[tud.robolab.model.Maze]] with the given `name` from this pool.
    *
    * @param name the name of the maze
    */
  def removeMaze(name: String)
  {
    pool.remove(name)
    setChanged()
    notifyObservers()
  }
}
