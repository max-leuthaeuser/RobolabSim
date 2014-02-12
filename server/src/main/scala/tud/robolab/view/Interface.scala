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

import scala.swing._
import javax.swing._
import tud.robolab.Boot
import javax.swing.border.BevelBorder
import java.awt.{BorderLayout, FlowLayout}
import java.awt.event.{MouseEvent, MouseAdapter, ActionEvent, ActionListener}
import tud.robolab.controller.{MapController, SessionController}
import tud.robolab.utils.SizeUtilities

object Interface extends SimpleSwingApplication
{
  private val CLOSE_TAB_ICON = new ImageIcon("img/closeTabButton.png")
  private val status = new Label("Waiting for connections ...")
  private val tabbed = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT)
  private val menu = createMenu()

  def top = new MainFrame
  {
    //Look and Feel
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    } catch {
      case e: Throwable => println(e)
    }

    val width = SizeUtilities.std_width
    val height = SizeUtilities.std_height
    val screenSize = SizeUtilities.getDisplaySize
    val dim = new java.awt.Dimension(width, height)
    val pos = new java.awt.Point((screenSize.width - width) / 2, (screenSize.height - height) / 2)
    location = pos
    minimumSize = dim
    preferredSize = dim
    maximumSize = dim
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    title = "RobolabSim"

    // create the status bar panel and shove it down the bottom of the frame
    val statusPanel = new BorderPanel()
    {
      border = new BevelBorder(BevelBorder.LOWERED)
      preferredSize = new java.awt.Dimension(width, 16)
      layout(status) = BorderPanel.Position.East
    }
    var mainPanel = new BorderPanel()
    mainPanel.layout(statusPanel) = BorderPanel.Position.South
    mainPanel.peer.add(tabbed, BorderLayout.CENTER)

    val mazeGenerator = new MazeGenerator
    val sessionsEditor = new SessionsView

    /** Add tabs here **/
    tabbed.addTab("MazeGenerator", mazeGenerator)
    tabbed.addTab("SessionsEditor", sessionsEditor)

    /** Attach Observers here **/
    MapController.mazePool.addObserver(mazeGenerator)
    SessionController.getSessions.addObserver(sessionsEditor)

    tabbed.addMouseListener(new PopupListener())
    contents = mainPanel

    override def closeOperation()
    {
      if (Dialogs.confirmation("Do you really want to exit the application and shut down the server?")) {
        Boot.terminate()
        super.closeOperation()
      }
    }
  }

  private def createMenu(): JPopupMenu =
  {
    val result = new JPopupMenu("Tabs")
    val item = new JMenuItem("Change layout")
    item.addActionListener(new ActionListener
    {
      def actionPerformed(e: ActionEvent)
      {
        tabbed.getTabPlacement match {
          case SwingConstants.TOP => tabbed.setTabPlacement(SwingConstants.RIGHT)
          case SwingConstants.RIGHT => tabbed.setTabPlacement(SwingConstants.BOTTOM)
          case SwingConstants.BOTTOM => tabbed.setTabPlacement(SwingConstants.LEFT)
          case SwingConstants.LEFT => tabbed.setTabPlacement(SwingConstants.TOP)
        }
      }
    })
    result.add(item)
    result
  }

  private class PopupListener extends MouseAdapter
  {
    override def mouseClicked(e: MouseEvent)
    {
      checkForPopup(e)
    }

    override def mousePressed(e: MouseEvent)
    {
      checkForPopup(e)
    }

    override def mouseReleased(e: MouseEvent)
    {
      checkForPopup(e)
    }

    private def checkForPopup(e: MouseEvent)
    {
      if (e.isPopupTrigger) {
        val c = e.getComponent
        menu.show(c, e.getX, e.getY)
      }
    }
  }

  def removeSimTap(c: SimulationView)
  {
    c.close()
    tabbed.remove(c)
  }

  private def _addSimTap(
    c: SimulationView,
    title: String)
  {
    MapController.mazePool.addObserver(c)
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

    btnClose.addActionListener(new ActionListener
    {
      def actionPerformed(e: ActionEvent)
      {
        Dialogs.closeOrBlock() match {
          case Dialog.Result.Yes =>
            c.close()
            tabbed.remove(c)
          case Dialog.Result.Cancel =>
            c.close(block = true)
            tabbed.remove(c)
          case _ =>
        }
      }
    })
    tabbed.setSelectedComponent(c)
  }

  def addSimTab(
    c: SimulationView,
    title: String,
    ask: Boolean = true): Boolean =
  {
    if (ask)
      Dialogs.addOrBlock(title) match {
        case Dialog.Result.Yes =>
          _addSimTap(c, title)
          return true
        case Dialog.Result.Cancel =>
          c.close(block = true)
          return false
        case _ => return false
      }
    else _addSimTap(c, title)
    true
  }
}
