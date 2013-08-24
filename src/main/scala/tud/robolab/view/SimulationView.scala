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
import tud.robolab.utils.IOUtils
import java.io.File
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import tud.robolab.model.{MazePool, Observer, Maze}
import spray.json._
import tud.robolab.model.MazeJsonProtocol._
import tud.robolab.controller.SessionManager
import tud.robolab.model.Session

class SimulationView(session: Session) extends JPanel with Observer[MazePool] {
  var isShown = true
  private val nameLabel = new JLabel()
  private val ipLabel = new JLabel(session.client.ip)
  private val box = new JComboBox(Interface.mazePool.mazeNames.toArray)
  private val listModel = new DefaultListModel[String]()

  private val settings = buildSettingsPanel

  private var content = new JScrollPane(buildMazePanel())

  private val mapsPanel = buildMapsPanel

  setLayout(new BorderLayout())
  add(settings, BorderLayout.WEST)
  add(content, BorderLayout.CENTER)
  add(mapsPanel, BorderLayout.EAST)

  def updateSession() {
    content.repaint()
    val p = session.latestPosition
    listModel.addElement("  x = " + p._1 + "; y = " + p._2 + "  ")
  }

  private def buildMapsPanel: JPanel = {
    val result = new JPanel()
    result.setLayout(new BorderLayout(0, 10))
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    box.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        val box = e.getSource.asInstanceOf[JComboBox[String]]
        if (box.getSelectedIndex != -1) {
          val n = box.getSelectedItem.asInstanceOf[String]
          session.maze = IOUtils.readFromFile(new File("maps/" + n + ".maze")).asJson.convertTo[Maze]
          nameLabel.setText(n)
          rebuild()
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

  private def buildSettingsPanel: JPanel = {
    val labelname = new JLabel("Maze ")
    val labelip = new JLabel("Client IP ")

    val edit = new JPanel(new GridLayout(2, 2, 5, 10))
    edit.add(labelname)
    edit.add(nameLabel)
    edit.add(labelip)
    edit.add(ipLabel)

    val result = new JPanel(new BorderLayout(0, 10))
    result.add(edit, BorderLayout.NORTH)
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    val list = new JList(listModel)
    list.setCellRenderer(new CustomCellRenderer())
    list.setFixedCellWidth(80)
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    list.setLayoutOrientation(JList.VERTICAL)
    list.setVisibleRowCount(-1)
    result.add(new JScrollPane(list), BorderLayout.CENTER)
    val clearBtn = new JButton("Clear")
    clearBtn.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        listModel.removeAllElements()
        session.clearWay
      }
    })

    result.add(clearBtn, BorderLayout.SOUTH)
    result
  }

  private def rebuild() {
    invalidate()
    remove(content)
    listModel.removeAllElements()
    content = new JScrollPane(buildMazePanel())
    add(content, BorderLayout.CENTER)
    validate()
  }

  private def buildMazePanel(): JPanel = {
    val result = new JPanel()
    result.setLayout(new GridLayout(session.maze.width, session.maze.height, 5, 5))
    session.maze.points.flatten.foreach(p => result.add(new Tile(p.get, readOnly = true)))
    result
  }

  override def receiveUpdate(subject: MazePool) {
    val listeners = box.getActionListeners
    box.removeActionListener(listeners(0))
    box.removeAllItems()
    subject.mazeNames.foreach(box.addItem)
    box.addActionListener(listeners(0))
  }
}

