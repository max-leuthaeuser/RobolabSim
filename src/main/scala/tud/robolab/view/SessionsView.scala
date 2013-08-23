package tud.robolab.view

import tud.robolab.model.Observer
import javax.swing._
import tud.robolab.controller.SessionManager.SessionPool
import java.awt.BorderLayout
import javax.swing.table.AbstractTableModel
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import tud.robolab.controller.SessionManager

class SessionsView extends JPanel with Observer[SessionPool] {
  private var model: Option[SessionPool] = Option.empty
  private val tableModel = new TableModel()

  setLayout(new BorderLayout())
  add(new JScrollPane(buildTable), BorderLayout.CENTER)

  private def buildTable: JTable = {
    val table = new JTable(tableModel)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

    val condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    val inputMap = table.getInputMap(condition)
    val actionMap = table.getActionMap()

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete")
    actionMap.put("Delete", new AbstractAction() {
      def actionPerformed(e: ActionEvent) {
        val row = table.getSelectedRow
        if (row != -1 && model.isDefined && !model.get.all.isEmpty) {
          model.get.remove(row)
        }
      }
    })

    table
  }

  private class TableModel extends AbstractTableModel {
    override def getRowCount: Int = if (model.isDefined) model.get.all.size else 1

    override def getColumnCount: Int = 2

    override def getValueAt(rowIndex: Int, columnIndex: Int): AnyRef = {
      if (model.isEmpty) columnIndex match {
        case 0 => ""
        case 1 => java.lang.Boolean.FALSE
      }
      else columnIndex match {
        case 0 => model.get.all.keys.toSeq(rowIndex).client.ip
        case 1 => java.lang.Boolean.valueOf(model.get.all.keys.toSeq(rowIndex).client.blocked)
      }
    }

    override def setValueAt(aValue: scala.Any, rowIndex: Int, columnIndex: Int) {
      super.setValueAt(aValue, rowIndex, columnIndex)
      if (model.isDefined && columnIndex == 1) model.get.all.keys.toSeq(rowIndex).client.blocked = aValue.asInstanceOf[Boolean]
    }

    override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columnIndex match {
      case 1 => true
      case _ => false
    }

    override def getColumnName(column: Int): String = column match {
      case 0 => "Client IP"
      case 1 => "Blocked?"
    }

    override def getColumnClass(columnIndex: Int): Class[_] = columnIndex match {
      case 0 => new String().getClass
      case 1 => java.lang.Boolean.TRUE.getClass
    }
  }

  def receiveUpdate(subject: SessionPool) {
    model = Option(subject)
    tableModel.fireTableDataChanged()
  }
}
