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

package tud.robolab.view

import javax.swing.JPanel
import java.awt._
import java.awt.event.{MouseListener, MouseEvent, MouseMotionListener}
import tud.robolab.model.Direction._
import tud.robolab.model.Point

class Tile(private val model: Point, private val cx: Int, private val cy: Int, private val readOnly: Boolean = false) extends JPanel {
  private def dim_width = this.getWidth

  private def dim_height = this.getHeight

  private var showHoverText = false

  setPreferredSize(new Dimension(60, 60))
  setMinimumSize(new Dimension(60, 60))

  setBackground(Color.WHITE)

  addMouseMotionListener(new MouseMotionListener {
    def mouseMoved(e: MouseEvent) {
      if (!readOnly) dirs.foreach(_ handleHover(e.getX, e.getY))
      showHoverText = true
    }

    def mouseDragged(e: MouseEvent) {}
  })

  addMouseListener(new MouseListener {
    def mouseExited(e: MouseEvent) {
      if (!readOnly) dirs.foreach(_ handleHover(-1, -1))
      showHoverText = false
    }

    def mouseClicked(e: MouseEvent) {}

    def mouseEntered(e: MouseEvent) {}

    def mousePressed(e: MouseEvent) {
      if (!readOnly) dirs.foreach(_ handleClick(e.getX, e.getY))
    }

    def mouseReleased(e: MouseEvent) {}
  })

  private sealed abstract class NodeTile {
    var sx, sy, width, height = 0
    var enabled = true
    private var hover = false

    var hoverColor = Color.darkGray
    var color = Color.lightGray

    def repaint(g: Graphics) {
      if (enabled)
        if (hover)
          g.setColor(hoverColor)
        else
          g.setColor(color)
      else
        g.setColor(Color.white)
      g.fillRect(sx, sy, width, height)
    }

    def handleClick(x: Int, y: Int) {
      val ex = sx + width
      val ey = sy + height
      if (x >= sx && x <= ex && y >= sy && y <= ey)
        enabled = !enabled
      Tile.this.repaint()
    }

    def handleHover(x: Int, y: Int) {
      val ex = sx + width
      val ey = sy + height
      if (x >= sx && x <= ex && y >= sy && y <= ey)
        hover = true
      else
        hover = false
      Tile.this.repaint()
    }
  }

  private class Token extends NodeTile {
    hoverColor = new Color(0, 100, 0)
    color = Color.green

    override def handleClick(x: Int, y: Int) {
      super.handleClick(x, y)
      if (!model.robot) model.token = enabled
    }

    override def repaint(g: Graphics) {
      sx = dim_width / 2 - (dim_width / 12)
      sy = dim_height / 2 - (dim_height / 12)
      width = dim_width / 6
      height = dim_height / 6
      enabled = model.token
      super.repaint(g)
    }
  }

  private class North extends NodeTile {
    enabled = model.has(NORTH)

    override def repaint(g: Graphics) {
      sx = dim_width / 2 - (dim_width / 12)
      width = dim_width / 6
      height = dim_height / 2 - (dim_height / 12)
      super.repaint(g)
    }

    override def handleClick(x: Int, y: Int) {
      super.handleClick(x, y)
      enabled match {
        case true => model + NORTH
        case false => model - NORTH
      }
    }
  }

  private class East extends NodeTile {
    enabled = model.has(EAST)

    override def repaint(g: Graphics) {
      sx = dim_width / 2 + (dim_width / 12)
      sy = dim_height / 2 - (dim_height / 12)
      width = dim_width
      height = dim_height / 6
      super.repaint(g)
    }

    override def handleClick(x: Int, y: Int) {
      super.handleClick(x, y)
      enabled match {
        case true => model + EAST
        case false => model - EAST
      }
    }
  }

  private class South extends NodeTile {
    enabled = model.has(SOUTH)

    override def repaint(g: Graphics) {
      sx = dim_width / 2 - (dim_width / 12)
      sy = dim_height / 2 + (dim_height / 12)
      width = dim_width / 6
      height = dim_height
      super.repaint(g)
    }

    override def handleClick(x: Int, y: Int) {
      super.handleClick(x, y)
      enabled match {
        case true => model + SOUTH
        case false => model - SOUTH
      }
    }
  }

  private class West extends NodeTile {
    enabled = model.has(WEST)

    override def repaint(g: Graphics) {
      sy = dim_height / 2 - (dim_height / 12)
      width = dim_width / 2 - (dim_width / 12)
      height = dim_height / 6
      super.repaint(g)
    }

    override def handleClick(x: Int, y: Int) {
      super.handleClick(x, y)
      enabled match {
        case true => model + WEST
        case false => model - WEST
      }
    }
  }

  private val dirs = Seq(new North(), new East(), new South(), new West(), new Token())

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    dirs.foreach(_ repaint g)
    if (model.robot) {
      g.setColor(Color.red)
      val _w = dim_width / 6
      val _h = dim_height / 6
      val x = dim_width / 2 - dim_width / 12
      val y = dim_height / 2 - dim_height / 12
      g.fillRect(x, y, _w, _h)
    }
    if (showHoverText) {
      g match {
        case g2: Graphics2D =>
          g.setColor(Color.black)
          g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
          g2.drawString("x: " + cx + ", y: " + cy, 2, 10)
        case _ =>
      }
    }
  }
}
