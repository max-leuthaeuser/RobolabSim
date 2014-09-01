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

package tud.robolab.view

import java.awt.event.MouseEvent
import java.awt.geom.Line2D
import java.awt.{Dimension, Polygon, RenderingHints, Color}
import java.util.{Observable, Observer}
import javax.swing.{ToolTipManager, JPanel, JLayeredPane, JPopupMenu}

import tud.robolab.model.{Point, Coordinate, Maze, Direction}
import tud.robolab.model.Direction._
import tud.robolab.utils.Relation

import scala.swing.event.{MouseEntered, MouseExited, MouseMoved, MouseClicked}
import scala.swing._

class MazeView(
  model: Maze,
  readOnly: Boolean = false
  ) extends JPanel with Observer
{

  private val gridPanel = new GridPanel(model.height, model.width)
  private val drawPanel = new DrawPanel()
  private val layerPanel = new JLayeredPane()
  layerPanel.setPreferredSize(new Dimension(Tile.DEF_WIDTH * model.width, Tile.DEF_HEIGHT * model.height))
  gridPanel.peer.setSize(layerPanel.getPreferredSize)
  gridPanel.peer.setLocation(0, 0)
  drawPanel.peer.setSize(layerPanel.getPreferredSize)
  drawPanel.peer.setLocation(0, 0)
  drawPanel.peer.setOpaque(false)
  layerPanel.add(gridPanel.peer, JLayeredPane.DEFAULT_LAYER)
  layerPanel.add(drawPanel.peer, JLayeredPane.PALETTE_LAYER)
  add(layerPanel)

  private val internalModel = scala.collection.mutable.HashMap[Coordinate, Tile]()

  (0 to model.height - 1).foreach(y => {
    (0 to model.width - 1).foreach(x => {
      val node = model.data.getOrElse(Coordinate(x, y), Point())
      val tile = new Tile(node, x, y, readOnly)
      val at = Coordinate(x, y)
      model.data(at) = node
      internalModel(at) = tile
      gridPanel.contents += tile
    })
  })
  model.addObserver(this)

  private object Tile
  {
    val DEF_WIDTH = 70
    val DEF_HEIGHT = 70
  }

  private class Tile(
    private val node: Point,
    private val coord_x: Int,
    private val coord_y: Int,
    private val readOnly: Boolean = false
    ) extends Panel
  {

    // define popup menu for setting coordinate system's root
    private class PopupMenu extends Component
    {
      override lazy val peer: JPopupMenu = new JPopupMenu

      def add(item: MenuItem): Unit =
      {
        peer.add(item.peer)
      }
    }

    private val item = new MenuItem(new Action("Set root at %s, %s".format(coord_x, -1 * coord_y))
    {
      def apply(): Unit =
      {
        model.origin = Coordinate(coord_x, coord_y)
        drawPanel.repaint()
      }
    })
    private val popup = new PopupMenu
    {
      add(item)
    }

    private def dim_width = peer.getWidth

    private def dim_height = peer.getHeight

    peer.setPreferredSize(new Dimension(Tile.DEF_WIDTH, Tile.DEF_HEIGHT))
    peer.setMinimumSize(new Dimension(Tile.DEF_WIDTH, Tile.DEF_HEIGHT))
    peer.setBackground(Color.WHITE)

    listenTo(mouse.clicks)
    listenTo(mouse.moves)

    reactions += {
      case event: MouseClicked => event.peer.getButton match {
        case MouseEvent.BUTTON1 => dirs.foreach(_ handleClick(event.point.x, event.point.y))
        case MouseEvent.BUTTON3 => popup.peer.show(event.source.peer, event.point.x, event.point.y)
        case _ => // fallback, do nothing
      }
      case MouseMoved(src, pt, mod) =>
        dirs.foreach(_ handleHover(pt.x, pt.y))
      case MouseExited(src, pt, mod) =>
        dirs.foreach(_ handleHover(-1, -1))
      case MouseEntered(src, pt, mod) =>
        peer.setToolTipText("%s".format(Relation.offset(Coordinate(coord_x, coord_y), model.origin)))
        ToolTipManager.sharedInstance().mouseMoved(
          new MouseEvent(this.peer, 0, 0, 0, 0, 0, 0, false))
    }

    private sealed abstract class NodeTile
    {
      var canvas_x, canvas_y, width, height = 0
      var enabled = true
      var hover = false

      var hoverColor = Color.darkGray
      var color = Color.lightGray

      def repaint(g: Graphics2D)
      {
        if (enabled)
          if (hover)
            g.setColor(hoverColor)
          else
            g.setColor(color)
        else
          g.setColor(Color.white)
        g.fillRect(canvas_x, canvas_y, width, height)
      }

      def handleClick(
        x: Int,
        y: Int
        )
      {
        if (!readOnly) {
          val ex = canvas_x + width
          val ey = canvas_y + height
          if (x >= canvas_x && x <= ex && y >= canvas_y && y <= ey)
            enabled = !enabled
        }
        Tile.this.repaint()
      }

      def handleHover(
        x: Int,
        y: Int
        )
      {
        val ex = canvas_x + width
        val ey = canvas_y + height
        if (x >= canvas_x && x <= ex && y >= canvas_y && y <= ey)
          hover = true
        else
          hover = false
        Tile.this.repaint()
      }

      def updateModel(dir: Direction)
      {
        val at = Coordinate(coord_x, coord_y)
        enabled match {
          case true =>
            node + dir
            Relation.neighbor[Point](model.data, at, dir).foreach(_ + Direction.oppositeOf(dir))
          case false =>
            node - dir
            Relation.neighbor[Point](model.data, at, dir).foreach(_ - Direction.oppositeOf(dir))
        }
        Relation.neighbor[Tile](internalModel, at, dir).foreach(_ repaint())
      }
    }

    private class Token extends NodeTile
    {
      hoverColor = new Color(0, 100, 0)
      color = Color.green

      override def handleClick(
        x: Int,
        y: Int
        )
      {
        super.handleClick(x, y)
        if (!node.robot) node.token = enabled
        model.setToken(Coordinate(coord_x, coord_y), enabled)
      }

      override def repaint(g: Graphics2D)
      {
        canvas_x = dim_width / 2 - (dim_width / 12)
        canvas_y = dim_height / 2 - (dim_height / 12)
        width = dim_width / 6
        height = dim_height / 6
        enabled = node.token
        super.repaint(g)
      }
    }

    private class North extends NodeTile
    {
      enabled = node.has(NORTH)

      override def repaint(g: Graphics2D)
      {
        canvas_x = dim_width / 2 - (dim_width / 12)
        width = dim_width / 6
        height = dim_height / 2 - (dim_height / 12)
        enabled = node.has(NORTH)
        super.repaint(g)
      }

      override def handleClick(
        x: Int,
        y: Int
        )
      {
        super.handleClick(x, y)
        updateModel(NORTH)
      }

      override def handleHover(
        x: Int,
        y: Int
        )
      {
        super.handleHover(x, y)
        val at = Coordinate(coord_x, coord_y)
        Relation.neighbor[Tile](internalModel, at, NORTH).foreach(n => {
          n.southTile.hover = hover
          n.repaint()
        })
      }
    }

    private class East extends NodeTile
    {
      enabled = node.has(EAST)

      override def repaint(g: Graphics2D)
      {
        canvas_x = dim_width / 2 + (dim_width / 12)
        canvas_y = dim_height / 2 - (dim_height / 12)
        width = dim_width
        height = dim_height / 6
        enabled = node.has(EAST)
        super.repaint(g)
      }

      override def handleClick(
        x: Int,
        y: Int
        )
      {
        super.handleClick(x, y)
        updateModel(EAST)
      }

      override def handleHover(
        x: Int,
        y: Int
        )
      {
        super.handleHover(x, y)
        val at = Coordinate(coord_x, coord_y)
        Relation.neighbor[Tile](internalModel, at, EAST).foreach(n => {
          n.westTile.hover = hover
          n.repaint()
        })
      }
    }

    private class South extends NodeTile
    {
      enabled = node.has(SOUTH)

      override def repaint(g: Graphics2D)
      {
        canvas_x = dim_width / 2 - (dim_width / 12)
        canvas_y = dim_height / 2 + (dim_height / 12)
        width = dim_width / 6
        height = dim_height
        enabled = node.has(SOUTH)
        super.repaint(g)
      }

      override def handleClick(
        x: Int,
        y: Int
        )
      {
        super.handleClick(x, y)
        updateModel(SOUTH)
      }

      override def handleHover(
        x: Int,
        y: Int
        )
      {
        super.handleHover(x, y)
        val at = Coordinate(coord_x, coord_y)
        Relation.neighbor[Tile](internalModel, at, SOUTH).foreach(n => {
          n.northTile.hover = hover
          n.repaint()
        })
      }
    }

    private class West extends NodeTile
    {
      enabled = node.has(WEST)

      override def repaint(g: Graphics2D)
      {
        canvas_y = dim_height / 2 - (dim_height / 12)
        width = dim_width / 2 - (dim_width / 12)
        height = dim_height / 6
        enabled = node.has(WEST)
        super.repaint(g)
      }

      override def handleClick(
        x: Int,
        y: Int
        )
      {
        super.handleClick(x, y)
        updateModel(WEST)
      }

      override def handleHover(
        x: Int,
        y: Int
        )
      {
        super.handleHover(x, y)
        val at = Coordinate(coord_x, coord_y)
        Relation.neighbor[Tile](internalModel, at, WEST).foreach(n => {
          n.eastTile.hover = hover
          n.repaint()
        })
      }
    }

    private val northTile = new North()
    private val eastTile = new East()
    private val southTile = new South()
    private val westTile = new West()
    private val dirs = Seq(northTile, eastTile, southTile, westTile, new Token())

    override def paint(g: Graphics2D)
    {
      {
        super.paintComponent(g)
        dirs.foreach(_ repaint g)
        if (node.robot) {
          g.setColor(Color.red)
          val _w = dim_width / 6
          val _h = dim_height / 6
          val x = dim_width / 2 - dim_width / 12
          val y = dim_height / 2 - dim_height / 12
          g.fillRect(x, y, _w, _h)
        }
      }
    }
  }

  private class DrawPanel extends Panel
  {
    override def paint(g: Graphics2D)
    {
      super.paint(g)

      // paint coordinate system

      // calculate origin coordinates
      val origin_x = model.origin.x * Tile.DEF_WIDTH + (Tile.DEF_WIDTH / 2)
      val origin_y = model.origin.y * Tile.DEF_HEIGHT + (Tile.DEF_WIDTH / 2)

      val to_x_x = peer.getWidth - 10
      val to_x_y = origin_y
      val to_y_x = origin_x
      val to_y_y = 10
      val line_x = new Line2D.Double(origin_x, origin_y, to_x_x, to_x_y)
      val line_y = new Line2D.Double(origin_x, origin_y, to_y_x, to_y_y)
      val arrowHead_x = new Polygon()
      arrowHead_x.addPoint(to_x_x, to_x_y - 5)
      arrowHead_x.addPoint(to_x_x, to_x_y + 5)
      arrowHead_x.addPoint(to_x_x + 9, to_x_y)
      val arrowHead_y = new Polygon()
      arrowHead_y.addPoint(to_y_x - 5, to_y_y)
      arrowHead_y.addPoint(to_y_x + 5, to_y_y)
      arrowHead_y.addPoint(to_y_x, to_y_y - 9)

      g.setColor(Color.darkGray)
      g.draw(line_x)
      g.draw(line_y)
      g.fillPolygon(arrowHead_x)
      g.fillPolygon(arrowHead_y)

      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.drawString("x", to_x_x, to_x_y + 15)
      g.drawString("y", to_y_x - 15, to_y_y)
    }
  }

  override def update(
    o: Observable,
    arg: scala.Any
    )
  {
    repaint()
  }
}