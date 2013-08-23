package tud.robolab.utils

import java.awt.{GraphicsEnvironment, Dimension}

object SizeUtilities {
  /**
   * @return the display size of the main screen at this computer.
   */
  def getDisplaySize: Dimension = {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment
    val gs = ge.getDefaultScreenDevice
    val dm = gs.getDisplayMode
    new Dimension(dm.getWidth, dm.getHeight)
  }
}