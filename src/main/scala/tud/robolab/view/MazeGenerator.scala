package tud.robolab.view

import javax.swing._
import java.awt.{GridLayout, BorderLayout}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeEvent, ChangeListener}
import tud.robolab.model.{Direction, Point, Maze}

class MazeGenerator extends JPanel {
  private var model = new Maze(Seq(Seq.empty))

  private var curr_width = 6
  private var curr_height = 6
  private val name = new JTextField("maze")

  setLayout(new BorderLayout())

  private val settings = buildSettingsPanel

  private var content = new JScrollPane(buildMazePanel())

  add(settings, BorderLayout.WEST)
  add(content, BorderLayout.CENTER)


  def buildSettingsPanel: JPanel = {
    val labelx = new JLabel("Width ")
    val labely = new JLabel("Height ")
    val labeln = new JLabel("Name ")

    val spinnerx = new JSpinner(new SpinnerNumberModel(curr_width, 2, 12, 1))
    spinnerx.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) {
        curr_width = spinnerx.getModel.asInstanceOf[SpinnerNumberModel].getNumber.intValue()
        rebuild()
      }
    })
    val spinnery = new JSpinner(new SpinnerNumberModel(curr_height, 2, 12, 1))
    spinnery.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) {
        curr_height = spinnery.getModel.asInstanceOf[SpinnerNumberModel].getNumber.intValue()
        rebuild()
      }
    })

    val edit = new JPanel(new GridLayout(3, 2, 5, 10))
    edit.add(labelx)
    edit.add(spinnerx)
    edit.add(labely)
    edit.add(spinnery)
    edit.add(labeln)
    edit.add(name)

    val result = new JPanel(new BorderLayout())
    result.add(edit, BorderLayout.NORTH)

    val okbtn = new JButton("Generate")
    okbtn.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        // TODO generate Maze ...
        println(model)
      }
    })

    result.add(okbtn, BorderLayout.SOUTH)
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    result
  }

  def rebuild() {
    invalidate()
    remove(content)
    content = new JScrollPane(buildMazePanel())
    add(content, BorderLayout.CENTER)
    validate()
  }

  def buildMazePanel(): JPanel = {
    val result = new JPanel()
    result.setLayout(new GridLayout(curr_width, curr_height, 5, 5))

    model = Maze((0 to curr_width - 1).map(x =>
      (0 to curr_height - 1).map {
        y => {
          val p = Option(Point(Direction.values.toSeq))
          result.add(new Tile(p.get))
          p
        }
      }.toSeq
    ).toSeq)

    result
  }
}
