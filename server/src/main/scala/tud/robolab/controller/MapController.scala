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

package tud.robolab.controller

import tud.robolab.view.SimulationView
import tud.robolab.model.{Maze, MazePool, Session}
import java.io.File
import tud.robolab.utils.IOUtils
import spray.json._
import tud.robolab.model.MazeJsonProtocol._
import tud.robolab.Config

object MapController
{
  val mazePool = new MazePool

  var hideMazes = Config.HIDE_MAZES

  def changeMap(
    m: String,
    session: Session,
    view: Option[SimulationView],
    remove: Boolean = true
    ): Boolean =
  {
    val f = new File(MazePool.STD_MAPS_FOLDER + m + MazePool.STD_MAPS_SUFFIX)
    f.isFile match {
      case true =>
        session.maze = IOUtils.readFromFile(f).asJson.convertTo[Maze]
        view.foreach(_ rebuild remove)
        if (remove) {
          session.clearWay()
          session.clearHistory()
        }
        true
      case _ => false
    }
  }
}
