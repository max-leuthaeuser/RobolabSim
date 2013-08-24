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

import spray.json._
import tud.robolab.model.MazeJsonProtocol._
import tud.robolab.utils.IOUtils
import java.io.File
import scala.collection.concurrent.TrieMap

class MazePool extends Subject[MazePool] {
  val pool = TrieMap[String, Maze]()

  IOUtils.createDirectory(new File("maps/"))
  IOUtils.getFileTreeFilter(new File("maps/"), ".maze").foreach(m => {
    pool(m) = IOUtils.readFromFile(new File("maps/" + m + ".maze")).asJson.convertTo[Maze]
  })

  def mazeNames: Iterable[String] = pool.keys

  def apply(name: String): Maze = pool(name)

  def +(name: String, maze: Maze) {
    pool(name) = maze
    notifyObservers()
  }

  def -(name: String) {
    pool.remove(name)
    notifyObservers()
  }
}
