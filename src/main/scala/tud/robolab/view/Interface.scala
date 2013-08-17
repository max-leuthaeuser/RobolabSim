package tud.robolab.view

import scala.swing._
import javax.swing._
import tud.robolab.Boot
import javax.swing.border.BevelBorder

object Interface extends SimpleSwingApplication {
  val status = new Label("Waiting for connections ...");

  def top = new MainFrame {
    //Look and Feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName);
    } catch {
      case e: Throwable => println(e)
    }

    val width = 700
    val height = 540
    val screenSize = java.awt.Toolkit.getDefaultToolkit.getScreenSize
    location = new java.awt.Point((screenSize.width - width) / 2, (screenSize.height - height) / 2)
    minimumSize = new java.awt.Dimension(width, height)
    preferredSize = new java.awt.Dimension(width, height)
    maximumSize = new java.awt.Dimension(width, height)
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    title = "RobolabSim"

    // create the status bar panel and shove it down the bottom of the frame
    val statusPanel = new BorderPanel() {
      border = new BevelBorder(BevelBorder.LOWERED)
      preferredSize = new java.awt.Dimension(width, 16)
      layout(status) = BorderPanel.Position.East
    }
    var mainPanel = new BorderPanel()
    mainPanel.layout(statusPanel) = BorderPanel.Position.South

    contents = mainPanel

    override def closeOperation() {
      if (Dialogs.confirmation("Do you really want to exit the application and shut down the server?")) {
        Boot.terminate
        super.closeOperation()
      }
    }
  }
}
