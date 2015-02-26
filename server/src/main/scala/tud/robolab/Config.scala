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

package tud.robolab

import com.typesafe.config.ConfigFactory

/** Loads and holds the configuration for the server */
object Config
{
  private val conf = ConfigFactory.load("application.conf")
  lazy val IP = conf.getString("spray.can.server.ip")
  lazy val PORT = conf.getInt("spray.can.server.port")
  lazy val HIDE_SWING = conf.getBoolean("spray.can.server.hide-swing")
  lazy val MAP = conf.getString("spray.can.server.default-map")
  lazy val ADMIN = conf.getString("spray.can.server.admin.pass")
  lazy val HIDE_MAZES = conf.getBoolean("spray.can.server.hide-mazes")
  lazy val MAZE_WIDTH = conf.getInt("spray.can.server.maze-width")
  lazy val MAZE_HEIGHT = conf.getInt("spray.can.server.maze-height")
}
