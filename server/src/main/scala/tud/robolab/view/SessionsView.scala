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

import java.util.{Observable, Observer}

import tud.robolab.model._
import javax.swing._
import java.awt.{Color, GridLayout, BorderLayout}
import javax.swing.table.AbstractTableModel
import java.awt.event.{MouseEvent, ActionListener, ActionEvent, KeyEvent}
import tud.robolab.controller.SessionController
import tud.robolab.utils.SizeUtilities
import tud.robolab.model.Session
import tud.robolab.model.Client

class SessionsView extends JPanel
                           with Observer
{
  private val tableModel = new TableModel()
  private val addBtn = new JButton("Add session")

  addBtn.addActionListener(new ActionListener
  {
    def actionPerformed(e: ActionEvent)
    {
      val r = SessionAddDialog.getSession
      if (r.isDefined) {
        SessionController.addSession(r.get)
        tableModel.fireTableDataChanged()
      }
    }
  })

  setLayout(new BorderLayout())
  add(new JScrollPane(buildTable), BorderLayout.CENTER)

  private val right = new JPanel(new BorderLayout())
  private val desc = new JLabel(
    "<html><div align=\"right\"><i>del</i> - delete the selected session<br /><i>o</i> - open the selected session</div></html>")
  private val pa = new JPanel(new BorderLayout(0, 10))
  pa.add(addBtn, BorderLayout.SOUTH)
  pa.add(desc, BorderLayout.CENTER)
  right.add(pa, BorderLayout.EAST)
  right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
  add(right, BorderLayout.SOUTH)

  private def buildTable: JTable =
  {
    val table = new JTable(tableModel)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    table.getColumnModel.getColumn(1).setMaxWidth(100)
    val condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    val inputMap = table.getInputMap(condition)
    val actionMap = table.getActionMap

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete")
    actionMap.put("Delete", new AbstractAction()
    {
      def actionPerformed(e: ActionEvent)
      {
        SessionController.removeSession(SessionController.getSession(table.getSelectedRow))
      }
    })

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), "Open")
    actionMap.put("Open", new AbstractAction()
    {
      def actionPerformed(e: ActionEvent)
      {
        val s = SessionController.getSession(table.getSelectedRow)
        val v = SessionController.getView(s).get
        if (!v.isShown) {
          v.isShown = true
          Interface.addSimTab(v, s.client.id, ask = false)
        }
      }
    })

    table.getColumnModel.getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField())
    {
      override def stopCellEditing(): Boolean =
      {
        val b = !getCellEditorValue.toString.isEmpty && !SessionController.hasSession(getCellEditorValue.toString)
        if (!b) {
          getComponent.setForeground(Color.red)
        } else {
          getComponent.setForeground(new Color(0, 100, 0))
          val s = SessionController.getSession(table.getSelectedRow)
          val v = SessionController.getView(s)
          SessionController.removeSession(s)
          s.client.id = getCellEditorValue.toString
          SessionController.set(s, v)
        }
        b
      }
    })

    table
  }

  private class TableModel extends AbstractTableModel
  {
    override def getRowCount: Int = if (!SessionController.hasSessions) SessionController.numberOfSessions() else 0

    override def getColumnCount: Int = 2

    override def getValueAt(
      rowIndex: Int,
      columnIndex: Int
      ): AnyRef =
    {
      if (SessionController.hasSessions) {
        columnIndex match {
          case 0 => ""
          case 1 => java.lang.Boolean.FALSE
        }
      }
      else {
        columnIndex match {
          case 0 => SessionController.getSession(rowIndex).client.id
          case 1 => java.lang.Boolean.valueOf(SessionController.getSession(rowIndex).client.blocked)
        }
      }
    }

    override def setValueAt(
      aValue: scala.Any,
      rowIndex: Int,
      columnIndex: Int
      )
    {
      super.setValueAt(aValue, rowIndex, columnIndex)
      if (columnIndex == 1) SessionController.getSession(rowIndex).client.blocked = aValue.asInstanceOf[Boolean]
    }

    override def isCellEditable(
      rowIndex: Int,
      columnIndex: Int
      ): Boolean = true

    override def getColumnName(column: Int): String = column match {
      case 0 => "Client IP"
      case 1 => "Blocked?"
    }

    override def getColumnClass(columnIndex: Int): Class[_] = columnIndex match {
      case 0 => new String().getClass
      case 1 => java.lang.Boolean.TRUE.getClass
    }
  }

  private object SessionAddDialog
  {
    def getSession: Option[Session] =
    {
      val d = new SessionAddDialog()
      d.get()
    }
  }

  private class SessionAddDialog
  {
    private var session: Option[Session] = Option.empty
    private val dialog = new JDialog()
    private val okBtn = new JButton("Ok")
    private val ipText = new JTextField()
    private val blockedBox = new JCheckBox("blocked")

    dialog.setTitle("New session")
    dialog.setResizable(false)
    dialog.setModal(true)
    dialog.setLayout(new BorderLayout())
    ipText.setToolTipText("Enter a valid group ID!")

    private val content = new JPanel(new GridLayout(3, 1))
    content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    content.add(new JLabel("ID"))
    content.add(ipText)
    content.add(blockedBox)

    okBtn.addActionListener(new ActionListener
    {
      def actionPerformed(e: ActionEvent)
      {
        if (!ipText.getText.isEmpty) {
          session = Option(Session(Client(ipText.getText, blockedBox.isSelected), Maze.empty, Seq.empty))
          dialog.setVisible(false)
          dialog.dispose()
        } else {
          ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(ipText, 0, 0, 0, 0, 0, 0, false))
        }
      }
    })


    dialog.add(content, BorderLayout.CENTER)
    dialog.add(okBtn, BorderLayout.SOUTH)

    dialog.pack()
    private val display = SizeUtilities.getDisplaySize
    private val posX = display.width / 2 - (dialog.getSize.width / 2)
    private val posY = display.height / 2 - (dialog.getSize.height / 2)
    dialog.setLocation(posX, posY)
    dialog.setVisible(true)

    private def get(): Option[Session] = session
  }

  override def update(
    o: Observable,
    arg: scala.Any
    ): Unit =
  {
    tableModel.fireTableDataChanged()
  }
}
