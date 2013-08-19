package tud.robolab.view

import javax.swing.JPanel
import java.awt.{Color, Dimension, Graphics}
import java.awt.event.{MouseListener, MouseEvent, MouseMotionListener}

class Tile extends JPanel {
  private def dim = getSize()

  setPreferredSize(new Dimension(60, 60))
  setMinimumSize(new Dimension(60, 60))

  setBackground(Color.WHITE)

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

  sealed abstract class NodeTile {
    var sx, sy, width, height = 0
    private var enabled = true
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
    override def repaint(g: Graphics) {
      sx = dim.width / 2 - (dim.width / 12)
      width = dim.width / 6
      height = dim.height / 2 - (dim.height / 12)
      super.repaint(g)
    }
  }

  class East extends NodeTile {
    override def repaint(g: Graphics) {
      sx = dim.width / 2 + (dim.width / 12)
      sy = dim.height / 2 - (dim.height / 12)
      width = dim.width
      height = dim.height / 6
      super.repaint(g)
    }
  }

  class South extends NodeTile {
    override def repaint(g: Graphics) {
      sx = dim.width / 2 - (dim.width / 12)
      sy = dim.height / 2 + (dim.height / 12)
      width = dim.width / 6
      height = dim.height
      super.repaint(g)
    }
  }

  class West extends NodeTile {
    override def repaint(g: Graphics) {
      sy = dim.height / 2 - (dim.height / 12)
      width = dim.width / 2 - (dim.width / 12)
      height = dim.height / 6
      super.repaint(g)
    }
  }

  val north = new North()
  val east = new East()
  val south = new South()
  val west = new West()
  val dirs = Seq(north, east, south, west)

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    dirs.foreach(_ repaint g)
  }
}
