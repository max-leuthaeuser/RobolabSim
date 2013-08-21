package tud.robolab.view

import javax.swing.JPanel
import java.awt.{Color, Dimension, Graphics}
import java.awt.event.{MouseListener, MouseEvent, MouseMotionListener}
import tud.robolab.model.Point
import tud.robolab.model.Direction._

class Tile(private val model: Point, private val readOnly: Boolean = false) extends JPanel {
  private def dim_width = this.getWidth

  private def dim_height = this.getHeight

  setPreferredSize(new Dimension(60, 60))
  setMinimumSize(new Dimension(60, 60))

  setBackground(Color.WHITE)

  if (!readOnly) {
    addMouseMotionListener(new MouseMotionListener {
      def mouseMoved(e: MouseEvent) {
        dirs.foreach(_ handleHover(e.getX, e.getY))
      }

      def mouseDragged(e: MouseEvent) {}
    })

    addMouseListener(new MouseListener {
      def mouseExited(e: MouseEvent) {
        dirs.foreach(_ handleHover(-1, -1))
      }

      def mouseClicked(e: MouseEvent) {}

      def mouseEntered(e: MouseEvent) {}

      def mousePressed(e: MouseEvent) {
        dirs.foreach(_ handleClick(e.getX, e.getY))
      }

      def mouseReleased(e: MouseEvent) {}
    })
  }

  sealed abstract class NodeTile {
    var sx, sy, width, height = 0
    var enabled = true
    private var hover = false

    def repaint(g: Graphics) {
      if (enabled)
        if (hover)
          g.setColor(Color.darkGray)
        else
          g.setColor(Color.lightGray)
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

  class North extends NodeTile {
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

  class East extends NodeTile {
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

  class South extends NodeTile {
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

  class West extends NodeTile {
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

  val dirs = Seq(new North(), new East(), new South(), new West())

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    dirs.foreach(_ repaint g)
    if (model.robot) {
      g.setColor(Color.red)
      val radius_w = dim_width / 6
      val radius_h = dim_height / 6
      val x = dim_width / 2 - dim_width / 12
      val y = dim_height / 2 - dim_height / 12
      g.fillOval(x, y, radius_w, radius_h)
    }
  }
}
