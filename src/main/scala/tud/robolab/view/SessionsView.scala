package tud.robolab.view

import tud.robolab.model.{Maze, Client, Session, Observer}
import javax.swing._
import tud.robolab.controller.SessionManager.SessionPool
import java.awt.{Color, GridLayout, BorderLayout}
import javax.swing.table.AbstractTableModel
import java.awt.event.{MouseEvent, ActionListener, ActionEvent, KeyEvent}
import tud.robolab.controller.SessionManager
import tud.robolab.utils.{IPUtils, SizeUtilities}

class SessionsView extends JPanel with Observer[SessionPool] {
  private val tableModel = new TableModel()
  private val addBtn = new JButton("Add session")
  addBtn.addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val r = SessionAddDialog.getSession
      if (r.isDefined) {
        SessionManager.addSession(r.get)
        tableModel.fireTableDataChanged()
      }
    }
  })

  setLayout(new BorderLayout())
  add(new JScrollPane(buildTable), BorderLayout.CENTER)

  private val right = new JPanel(new BorderLayout())
  private val desc = new JLabel("<html><div align=\"right\"><i>del</i> - delete the selected session<br /><i>o</i> - open the selected session</div></html>")
  private val pa = new JPanel(new BorderLayout(0, 10))
  pa.add(addBtn, BorderLayout.SOUTH)
  pa.add(desc, BorderLayout.CENTER)
  right.add(pa, BorderLayout.EAST)
  right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
  add(right, BorderLayout.SOUTH)

  private def buildTable: JTable = {
    val table = new JTable(tableModel)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

    val condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    val inputMap = table.getInputMap(condition)
    val actionMap = table.getActionMap

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete")
    actionMap.put("Delete", new AbstractAction() {
      def actionPerformed(e: ActionEvent) {
        SessionManager.removeSession(SessionManager.getSession(table.getSelectedRow))
      }
    })

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), "Open")
    actionMap.put("Open", new AbstractAction() {
      def actionPerformed(e: ActionEvent) {
        val s = SessionManager.getSession(table.getSelectedRow)
        val v = SessionManager.sessions.get(s)
        if (!v.isShown) {
          v.isShown = true
          Interface.addSimTab(v, s.client.ip, ask = false)
        }
      }
    })

    table.getColumnModel.getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
      override def stopCellEditing(): Boolean = {
        val b = IPUtils.isValidIPV4(getCellEditorValue.toString) && !SessionManager.hasSession(getCellEditorValue.toString)
        if (!b) {
          getComponent.setForeground(Color.red)
        } else {
          getComponent.setForeground(new Color(0, 100, 0))
          val s = SessionManager.getSession(table.getSelectedRow)
          val v = SessionManager.sessions.get(s)
          SessionManager.removeSession(s)
          s.client.ip = getCellEditorValue.toString
          SessionManager.sessions.set(s, v)
        }
        b
      }
    })

    table
  }

  private class TableModel extends AbstractTableModel {
    override def getRowCount: Int = if (!SessionManager.sessions.all.isEmpty) SessionManager.sessions.all.size else 0

    override def getColumnCount: Int = 2

    override def getValueAt(rowIndex: Int, columnIndex: Int): AnyRef = {
      if (SessionManager.sessions.all.isEmpty) columnIndex match {
        case 0 => ""
        case 1 => java.lang.Boolean.FALSE
      }
      else columnIndex match {
        case 0 => SessionManager.getSession(rowIndex).client.ip
        case 1 => java.lang.Boolean.valueOf(SessionManager.getSession(rowIndex).client.blocked)
      }
    }

    override def setValueAt(aValue: scala.Any, rowIndex: Int, columnIndex: Int) {
      super.setValueAt(aValue, rowIndex, columnIndex)
      if (columnIndex == 1) SessionManager.getSession(rowIndex).client.blocked = aValue.asInstanceOf[Boolean]
    }

    override def isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

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
    tableModel.fireTableDataChanged()
  }

  private object SessionAddDialog {
    def getSession: Option[Session] = {
      val d = new SessionAddDialog()
      d.get()
    }
  }

  private class SessionAddDialog {
    private var session: Option[Session] = Option.empty
    private val dialog = new JDialog()
    private val okBtn = new JButton("Ok")
    private val ipText = new JTextField()
    private val blockedBox = new JCheckBox("blocked")

    dialog.setTitle("New session")
    dialog.setResizable(false)
    dialog.setModal(true)
    dialog.setLayout(new BorderLayout())
    ipText.setToolTipText("Enter a valid IP address!")

    private val content = new JPanel(new GridLayout(3, 1))
    content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
    content.add(new JLabel("IP"))
    content.add(ipText)
    content.add(blockedBox)

    okBtn.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        if (IPUtils.isValidIPV4(ipText.getText)) {
          session = Option(Session(Client(ipText.getText, blockedBox.isSelected), Maze.empty, Seq.empty))
          dialog.setVisible(false)
          dialog.dispose()
        } else
          ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(ipText, 0, 0, 0, 0, 0, 0, false))
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

}
