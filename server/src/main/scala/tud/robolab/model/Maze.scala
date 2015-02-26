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

import PointJsonProtocol._
import spray.json._
import Direction._
import tud.robolab.controller.MapController
import tud.robolab.Config
import tud.robolab.utils.Relation

import scala.collection.mutable
import scala.util.Random

/** Class representing a maze. Points (see [[tud.robolab.model.Point]]) are stored in a Map with their
  * corresponding [[tud.robolab.model.Coordinate]].
  *
  * @param width the initial width of the maze
  * @param height the initial height of the maize
  * @param data the initial map of points ([[tud.robolab.model.Point]]) representing all intersections
  */
class Maze(
  val width: Int,
  val height: Int,
  val data: mutable.HashMap[Coordinate, Point] = mutable.HashMap.empty,
  var origin: Coordinate = Maze.DEFAULT_ORIGIN
  ) extends Observable
{
  /**
   * Get a [[tud.robolab.model.Point]] from this Maze.
   * @param at the desired [[tud.robolab.model.Coordinate]]
   * @return an Option containing the [[tud.robolab.model.Point]]
   */
  def getPoint(at: Coordinate): Option[Point] = data.get(Coordinate(at.x + origin.x, -1 * at.y + origin.y))

  /** Check if parameters `x` and `y` describe a valid position.
    *
    * @param at the [[tud.robolab.model.Coordinate]] to check
    * @return true if `at` is within the maze, false otherwise
    */
  def isValid(at: Coordinate): Boolean = data.contains(Coordinate(at.x + origin.x, -1 * at.y + origin.y))

  /**
   * Apply the given function for some specific [[tud.robolab.model.Coordinate]].
   *
   * @param at the [[tud.robolab.model.Coordinate]]
   * @param func the function to execute at the [[tud.robolab.model.Coordinate]] `at`.
   * @return `true` if this [[tud.robolab.model.Maze]] contains the [[tud.robolab.model.Coordinate]] `at`, `false` otherwise.
   */
  private def saveSetAt(
    at: Coordinate,
    func: () => Unit
    ): Boolean =
  {
    if (data.contains(at)) {
      func()
      setChanged()
      notifyObservers()
      true
    } else {
      false
    }
  }

  /**
   * Set the robot at the given [[tud.robolab.model.Coordinate]].
   *
   * @param at the [[tud.robolab.model.Coordinate]]
   * @return `true` if the robot could be placed successfully, `false` otherwise.
   */
  def setRobot(at: Coordinate): Boolean =
  {
    val t_at = Coordinate(at.x + origin.x, -1 * at.y + origin.y)
    saveSetAt(t_at, () => {
      data.values.foreach(_.robot = false)
      data(t_at).robot = true
    })
  }

  /**
   * Set the token at the given [[tud.robolab.model.Coordinate]].
   *
   * @param at the [[tud.robolab.model.Coordinate]]
   * @param token boolean value if the token should be set or not.
   * @return `true` if the token could be placed successfully, `false` otherwise.
   */
  def setToken(
    at: Coordinate,
    token: Boolean
    ): Boolean =
  {
    saveSetAt(at, () => {
      getPoint(at).foreach(_.token = token)
    })
  }

  /**
   * Set the origin at the given [[tud.robolab.model.Coordinate]].
   *
   * @param at the [[tud.robolab.model.Coordinate]]
   * @return `true` if the origin could be placed successfully, `false` otherwise.
   */
  def setOrigin(at: Coordinate): Boolean =
  {
    val t_at = Coordinate(at.x, -1 * at.y)
    saveSetAt(t_at, () => {
      origin = t_at
    })
  }

  /**
   * @return the number of tokens in this maze
   */
  def getNumberOfToken: Int = data.values.count(_.token)

  /**
   * @return the points of this maze es Seq of Seqs.
   */
  def pointsAsSeq: Seq[Seq[Option[Point]]] =
  {
    (0 to height - 1).map(y => {
      (0 to width - 1).map(x => {
        data.get(Coordinate(x, y))
      })
    })
  }

  /**
   * @return an HTML String representing this maze.
   */
  def asHtml: String =
    pointsAsSeq.map(xs => {
      var a = xs.map {
        case Some(p) if p.has(NORTH) => "|   "
        case Some(p) => "    "
        case None => "     "
      }.mkString

      a = a.trim.isEmpty match {
        case true => ""
        case false => a + "<br/>"
      }

      val b =
        xs.map {
          case Some(p) if p.directions.nonEmpty =>
            val core = p.token match {
              case true => "O"
              case false => "X"
            }

            val dir = p.has(WEST) match {
              case true => "---"
              case false => "   "
            }
            dir + core
          case _ => "    "
        }.mkString

      var res = b
      if (b.startsWith("   ")) {
        res = b.substring(3)
      }
      a + res
    }).mkString("<br/>").replaceAll(" ", "&nbsp;")
}

/** Companion object for [[tud.robolab.model.Maze]] functioning as factory. */
object Maze
{
  val DEFAULT_WIDTH = Config.MAZE_WIDTH
  val DEFAULT_HEIGHT = Config.MAZE_HEIGHT
  val DEFAULT_ORIGIN = Coordinate(0, 0)

  /**
   * @param width the width of the new maze
   * @param height the height of the new maze
   * @return a new [[tud.robolab.model.Maze]] with the given `width` and `height`
   */
  def empty(
    width: Int,
    height: Int
    ): Maze =
  {
    val max_x = width - 1
    val max_y = height - 1

    val initialMap = mutable.HashMap[Coordinate, Point]()

    (0 to max_y).foreach(y =>
      (0 to max_x).foreach(x => {
        val p = (x, y) match {
          case (0, 0) => initialMap(Coordinate(0, 0)) = Point(Seq(SOUTH, EAST))
          case (xs, xy) if xs == max_x && xy == max_y => initialMap(Coordinate(xs, xy)) = Point(Seq(NORTH, WEST))
          case (xs, xy) if xs == 0 && xy == max_y => initialMap(Coordinate(xs, xy)) = Point(Seq(NORTH, EAST))
          case (xs, xy) if xs == max_x && xy == 0 => initialMap(Coordinate(xs, xy)) = Point(Seq(WEST, SOUTH))
          case (xs, xy) if xs == max_x => initialMap(Coordinate(xs, xy)) = Point(Seq(NORTH, SOUTH, WEST))
          case (xs, xy) if xy == max_y => initialMap(Coordinate(xs, xy)) = Point(Seq(EAST, WEST, NORTH))
          case (xs, xy) if xs == 0 => initialMap(Coordinate(xs, xy)) = Point(Seq(SOUTH, EAST, NORTH))
          case (xs, xy) if xy == 0 => initialMap(Coordinate(xs, xy)) = Point(Seq(SOUTH, EAST, WEST))
          case _ => initialMap(Coordinate(x, y)) = Point()
        }
      })
    )

    new Maze(width, height, initialMap)
  }

  /**
   * @return a new [[tud.robolab.model.Maze]] with `width` and `height` from [[tud.robolab.Config]]
   */
  def empty: Maze = empty(DEFAULT_WIDTH, DEFAULT_HEIGHT)

  /**
   * @return a new default maze from configuration or a new [[tud.robolab.model.Maze]] if configuration is invalid
   */
  def default: Maze = MapController.mazePool.mazeNames.contains(Config.MAP) match {
    case true => MapController.mazePool(Config.MAP)
    case false => empty
  }

  /**
   * Build a random maze using the prim algorithm and adding tokens and loops afterwards.
   *
   * @param width the width of the new maze
   * @param height the height of the new maze
   * @return a new randomly layouted [[tud.robolab.model.Maze]] with the given `width` and `height`
   */
  def random(
    width: Int,
    height: Int
    ): Maze =
  {
    def randomCoordinate: Coordinate =
    {
      val start = 0
      val end_x = width - 1
      val end_y = height - 1
      val rnd = new Random()
      Coordinate(start + rnd.nextInt((end_x - start) + 1), start + rnd.nextInt((end_y - start) + 1))
    }

    def addEdge(
      maze: mutable.HashMap[Coordinate, Point],
      a: Coordinate,
      b: Coordinate
      )
    {
      if (a.y - 1 == b.y) {
        // B is in the south of A
        maze(b).addDirection(SOUTH)
        maze(a).addDirection(NORTH)
      }
      if (a.y + 1 == b.y) {
        // B is in the north of A
        maze(a).addDirection(SOUTH)
        maze(b).addDirection(NORTH)
      }
      if (a.x + 1 == b.x) {
        // B is in the east of A
        maze(a).addDirection(EAST)
        maze(b).addDirection(WEST)
      }
      if (a.x - 1 == b.x) {
        // B is in the west of A
        maze(b).addDirection(EAST)
        maze(a).addDirection(WEST)
      }
    }

    val max_x = width - 1
    val max_y = height - 1

    val initialMap = mutable.HashMap[Coordinate, Point]()

    (0 to max_y).foreach(y =>
      (0 to max_x).foreach(x => {
        initialMap(Coordinate(x, y)) = Point(Seq.empty)
      })
    )

    val finalMap = mutable.HashMap[Coordinate, Point]()
    val wallList = mutable.ListBuffer[(Coordinate, Coordinate)]()
    var rndCoord = randomCoordinate

    finalMap(rndCoord) = initialMap(rndCoord).copy()
    Relation.neighborsCoordinate(rndCoord)
      .filter(n => n.x >= 0 && n.x <= max_x && n.y >= 0 && n.y <= max_y)
      .foreach(n => {
      wallList += ((rndCoord, n))
    })

    while (wallList.nonEmpty) {
      val rndWall = Random.shuffle(wallList).head
      if (!finalMap.contains(rndWall._2)) {
        finalMap(rndWall._2) = initialMap(rndWall._2).copy()
        addEdge(finalMap, rndWall._1, rndWall._2)
        Relation.neighborsCoordinate(rndWall._2)
          .filter(n => n.x >= 0 && n.x <= max_x && n.y >= 0 && n.y <= max_y)
          .foreach(n => {
          wallList += ((rndWall._2, n))
        })
      }
      wallList -= ((rndWall._1, rndWall._2))
    }

    var tokens = 0
    val max_tokens = Math.max(width * height / 10 + 1, 2)
    while (tokens < max_tokens) {
      rndCoord = randomCoordinate
      finalMap(rndCoord).token = true
      tokens = tokens + 1
      // place some loops:
      val r1 = randomCoordinate
      var r2 = Relation.neighborCoordinate(r1, Random.shuffle(Direction.values.toSeq).head)
      while (r2.x < 0 || r2.x > max_x || r2.y < 0 || r2.y > max_y) {
        r2 = Relation.neighborCoordinate(r1, Random.shuffle(Direction.values.toSeq).head)
      }

      addEdge(finalMap, r1, r2)
    }
    new Maze(width, height, finalMap)
  }
}

/** Implicit conversions from [[tud.robolab.model.Maze]] to json. */
object MazeJsonProtocol extends DefaultJsonProtocol
{

  implicit object MazeJsonFormat extends RootJsonFormat[Maze]
  {
    def write(p: Maze): JsArray =
    {
      val points = p.pointsAsSeq.map(ys => {
        ys.map {
          case None => JsString("None")
          case Some(e) => e.toJson
        }.toList
      }).toVector

      JsArray(points.map(JsArray(_:_*)))
    }

    def read(value: JsValue): Maze = value match {
      case s: JsArray =>
        try {
          val initialMap = mutable.HashMap[Coordinate, Point]()
          val width = s.elements(0).asInstanceOf[JsArray].elements.size
          val height = s.elements.size

          (0 to height - 1).foreach(y => {
            (0 to width - 1).foreach(x => {
              val elem = s.elements(y).asInstanceOf[JsArray].elements(x)
              if (!elem.compactPrint.contains("None")) {
                initialMap(Coordinate(x, y)) = elem.convertTo[Point]
              }
            })
          })

          new Maze(width, height, initialMap)
        } catch {
          case e: Exception => deserializationError("Valid maze expected!", e)
        }
      case _ => deserializationError("Valid maze expected!")
    }
  }

}
