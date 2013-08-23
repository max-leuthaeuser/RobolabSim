package tud.robolab.view

import scala.swing._
import javax.swing._
import tud.robolab.Boot
import javax.swing.border.BevelBorder
import java.awt.{BorderLayout, FlowLayout}
import java.awt.event.{ActionEvent, ActionListener}
import tud.robolab.model.MazePool
import tud.robolab.controller.SessionManager

object Interface extends SimpleSwingApplication {
  private val CLOSE_TAB_ICON = new ImageIcon("img/closeTabButton.png")
  val status = new Label("Waiting for connections ...")
  val tabbed = new JTabbedPane
  var mazePool: MazePool = null

  def top = new MainFrame {
    //Look and Feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
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
    mainPanel.peer.add(tabbed, BorderLayout.CENTER)

    /** Initialize MazePool and MazeGenerator **/
    mazePool = new MazePool
    val mazeGenerator = new MazeGenerator
    val sessionsEditor = new SessionsView

    /** Add tabs here **/
    tabbed.addTab("MazeGenerator", mazeGenerator)
    tabbed.addTab("SessionsEditor", sessionsEditor)

    /** Attach Observers here **/
    mazePool.addObserver(mazeGenerator)
    SessionManager.sessions.addObserver(sessionsEditor)

    contents = mainPanel

    override def closeOperation() {
      if (Dialogs.confirmation("Do you really want to exit the application and shut down the server?")) {
        Boot.terminate
        super.closeOperation()
      }
    }
  }

  def removeSimTap(c: SimulationView) {
    c.close()
    tabbed.remove(c)
  }

  private def _addSimTap(c: SimulationView, title: String) {
    mazePool.addObserver(c)
    tabbed.addTab(null, c)
    val pos = tabbed.indexOfComponent(c)

    val pnlTab = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0))
    pnlTab.setOpaque(false)
    pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0))

    val lblTitle = new JLabel(title)

    val btnClose = new JButton()
    btnClose.setOpaque(false)
    btnClose.setIcon(CLOSE_TAB_ICON)
    btnClose.setBorder(null)
    btnClose.setFocusable(false)

    pnlTab.add(lblTitle)
    pnlTab.add(btnClose)

    tabbed.setTabComponentAt(pos, pnlTab)

    btnClose.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        Dialogs.closeOrBlock() match {
          case Dialog.Result.Yes => {
            c.close()
            tabbed.remove(c)
          }
          case Dialog.Result.Cancel => {
            c.close(block = true)
            tabbed.remove(c)
          }
          case _ =>
        }
      }
    })
    tabbed.setSelectedComponent(c)
  }

  def addSimTab(c: SimulationView, title: String, ask: Boolean = true): Boolean = {
    if (ask)
      Dialogs.addOrBlock(title) match {
        case Dialog.Result.Yes => {
          _addSimTap(c, title)
          return true
        }
        case Dialog.Result.Cancel => {
          c.close(block = true)
          return false
        }
        case _ => return false
      }
    else _addSimTap(c, title)
    true
  }
}
