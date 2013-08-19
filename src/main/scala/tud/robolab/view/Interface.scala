package tud.robolab.view

import scala.swing._
import javax.swing._
import tud.robolab.Boot
import javax.swing.border.BevelBorder
import scala.swing.TabbedPane.Page
import java.awt.{GridLayout, BorderLayout, LayoutManager, FlowLayout}
import scala.swing.event.ButtonClicked
import java.awt.event.{ActionEvent, ActionListener}

object Interface extends SimpleSwingApplication {
  private val CLOSE_TAB_ICON = new ImageIcon("img/closeTabButton.png")
  val status = new Label("Waiting for connections ...")
  val tabbed = new JTabbedPane

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

    val gpanel = new JPanel(new GridLayout(8,8,5,5))
    var i = 0
    while (i < 64) {
      gpanel.add(new Tile())
      i+=1
    }
    addTab(new JScrollPane(gpanel), "Tiletest")

    contents = mainPanel

    override def closeOperation() {
      if (Dialogs.confirmation("Do you really want to exit the application and shut down the server?")) {
        Boot.terminate
        super.closeOperation()
      }
    }
  }

  def addTab(c: JComponent, title: String) {
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
        if (Dialogs.confirmation("Do you really want to close this tab?"))
          tabbed.remove(c)
      }
    })
    tabbed.setSelectedComponent(c)
  }
}
