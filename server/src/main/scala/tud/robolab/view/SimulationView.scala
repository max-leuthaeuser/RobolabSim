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

import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import tud.robolab.model.{MazePool, Observer}
import tud.robolab.controller.{MainController, SessionManager}
import tud.robolab.model.Session

class SimulationView(session: Session, var isShown: Boolean = true) extends JPanel with Observer[MazePool] {
  private val box = new JComboBox(MainController.mazePool.mazeNames.toArray)
  private val listModel = new DefaultListModel[String]()

  private val settings = buildSettingsPanel

  private var content = new JScrollPane(buildMazePanel())

  private val mapsPanel = buildMapsPanel()

  private val splitPane: JSplitPane = buildSplitPane()

  setLayout(new BorderLayout())
  add(splitPane, BorderLayout.CENTER)

  def updateSession() {
    content.repaint()
    val p = session.latestPosition
    val t = if (!p.token) "" else "(T)"
    listModel.addElement(" [%s] x = %s; y = %s %s ".format(p.time, p.x, p.y, t))
  }

  private def buildSplitPane(): JSplitPane = {
    val panel = new JPanel()
    panel.setLayout(new BorderLayout())
    panel.add(content, BorderLayout.CENTER)
    panel.add(mapsPanel, BorderLayout.EAST)
    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, settings, panel)
  }

  private def buildMapsPanel(): JPanel = {
    val result = new JPanel()
    result.setLayout(new BorderLayout(0, 10))
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    box.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        val box = e.getSource.asInstanceOf[JComboBox[String]]
        if (box.getSelectedIndex != -1) {
          MainController.changeMap(box.getSelectedItem.asInstanceOf[String], session, Option(SimulationView.this))
        }
      }
    })

    result.add(box, BorderLayout.NORTH)
    result
  }

  def close(block: Boolean = false) {
    SessionManager.blockSession(session.client.ip, block)
    isShown = false
  }

  private def buildSettingsPanel(): JPanel = {
    val result = new JPanel(new BorderLayout(0, 10))
    result.setMinimumSize(new Dimension(230, 100))
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    val list = new JList(listModel)
    list.setCellRenderer(new CustomCellRenderer())
    list.setFixedCellWidth(165)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    list.setLayoutOrientation(JList.VERTICAL)
    list.setVisibleRowCount(-1)
    result.add(new JScrollPane(list), BorderLayout.CENTER)
    val clearBtn = new JButton("Clear")
    clearBtn.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        listModel.removeAllElements()
        session.clearWay()
      }
    })

    result.add(clearBtn, BorderLayout.SOUTH)
    result
  }

  def rebuild() {
    invalidate()
    splitPane.getRightComponent match {
      case p: JPanel => {
        p.remove(content)
        content = new JScrollPane(buildMazePanel())
        p.add(content, BorderLayout.CENTER)
      }
      case _ =>
    }
    listModel.removeAllElements()
    validate()
  }

  private def buildMazePanel(): JPanel = {
    val result = new JPanel()
    result.setLayout(new GridLayout(session.maze.width, session.maze.height, 5, 5))
    session.maze.points.view.zipWithIndex.foreach(xs => {
      xs._1.view.zipWithIndex.foreach(p => {
        result.add(new Tile(p._1.get, xs._2, p._2, readOnly = true))
      })
    })
    result
  }

  override def receiveUpdate(subject: MazePool) {
    val listeners = box.getActionListeners
    box.removeActionListener(listeners(0))
    box.removeAllItems()
    subject.mazeNames.foreach(box.addItem)
    box.addActionListener(listeners(0))
  }

  private class CustomCellRenderer extends ListCellRenderer[String] {
    private val peerRenderer: ListCellRenderer[String] = (new DefaultListCellRenderer).asInstanceOf[ListCellRenderer[String]]
    private val color = new Color(245, 245, 245)

    override def getListCellRendererComponent(list: JList[_ <: String], cell: String, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component = {
      val component = peerRenderer.getListCellRendererComponent(list, cell, index, isSelected, cellHasFocus).asInstanceOf[JComponent]
      component.setForeground(Color.black)
      if (index % 2 == 0)
        component.setBackground(color)
      else
        component.setBackground(Color.white)
      component
    }
  }

}

