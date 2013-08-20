package tud.robolab.view

import javax.swing._
import tud.robolab.utils.IOUtils
import java.io.File
import java.awt.{GridLayout, BorderLayout}
import java.awt.event.{ActionEvent, ActionListener}
import tud.robolab.model.{MazePool, Observer, Maze}
import spray.json._
import tud.robolab.model.MazeJsonProtocol._

class SimulationView extends JPanel with Observer[MazePool] {
  private var model: Maze = Maze.empty

  private val nameLabel = new JLabel()
  private val ipLabel = new JLabel()
  private val box = new JComboBox(Interface.mazePool.mazeNames.toArray)

  private val settings = buildSettingsPanel

  private var content = new JScrollPane(buildMazePanel())

  private val mapsPanel = buildMapsPanel

  setLayout(new BorderLayout())
  add(settings, BorderLayout.WEST)
  add(content, BorderLayout.CENTER)
  add(mapsPanel, BorderLayout.EAST)

  private def buildMapsPanel: JPanel = {
    val result = new JPanel()
    result.setLayout(new BorderLayout())
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    box.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        val box = e.getSource.asInstanceOf[JComboBox[String]]
        if (box.getSelectedIndex != -1) {
          val n = box.getSelectedItem.asInstanceOf[String]
          model = IOUtils.readFromFile(new File("maps/" + n + ".maze")).asJson.convertTo[Maze]
          nameLabel.setText(n)
          rebuild()
        }
      }
    })

    result.add(box, BorderLayout.NORTH)
    result
  }

  private def buildSettingsPanel: JPanel = {
    val labelname = new JLabel("Maze ")
    val labelip = new JLabel("Client IP ")

    val edit = new JPanel(new GridLayout(2, 2, 5, 10))
    edit.add(labelname)
    edit.add(nameLabel)
    edit.add(labelip)
    edit.add(ipLabel)

    val result = new JPanel(new BorderLayout())
    result.add(edit, BorderLayout.NORTH)
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    result
  }

  private def rebuild() {
    invalidate()
    remove(content)
    content = new JScrollPane(buildMazePanel())
    add(content, BorderLayout.CENTER)
    validate()
  }

  private def buildMazePanel(): JPanel = {
    val result = new JPanel()
    result.setLayout(new GridLayout(model.width, model.height, 5, 5))
    model.points.flatten.foreach(p => result.add(new Tile(p.get, readOnly = true)))
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

