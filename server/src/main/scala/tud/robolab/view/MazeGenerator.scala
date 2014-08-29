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

import java.io.File
import javax.swing._
import java.awt.{GridLayout, BorderLayout}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeEvent, ChangeListener}
import tud.robolab.model.{MazePool, Observer, Maze}
import tud.robolab.controller.MapController
import tud.robolab.utils.IOUtils
import spray.json._
import tud.robolab.model.MazeJsonProtocol._

class MazeGenerator extends JPanel
                            with Observer[MazePool]
{
  private var model: Maze = null

  private var curr_width = 7
  private var curr_height = 7

  private val name = new JTextField("maze")
  private val box = new JComboBox(MapController.mazePool.mazeNames.toArray)
  private val spinnerx = new JSpinner(new SpinnerNumberModel(curr_width, 2, 30, 1))
  private val spinnery = new JSpinner(new SpinnerNumberModel(curr_height, 2, 30, 1))
  private val numTokensLabel = TokenLabel()

  private val settings = buildSettingsPanel

  private var content = new JScrollPane(buildMazePanel())

  private val mapsPanel = buildMapsPanel

  setLayout(new BorderLayout())
  add(settings, BorderLayout.WEST)
  add(content, BorderLayout.CENTER)
  add(mapsPanel, BorderLayout.EAST)

  private def buildMapsPanel: JPanel =
  {
    val result = new JPanel()
    result.setLayout(new BorderLayout())
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))

    box.addActionListener(new ActionListener
    {
      def actionPerformed(e: ActionEvent)
      {
        val box = e.getSource.asInstanceOf[JComboBox[String]]
        if (box.getSelectedIndex != -1) {
          val n = box.getSelectedItem.asInstanceOf[String]
          model = IOUtils.readFromFile(new File("maps/" + n + ".maze")).asJson.convertTo[Maze]
          curr_width = model.width
          curr_height = model.height
          spinnerx.setValue(curr_width)
          spinnery.setValue(curr_height)
          name.setText(n)
          rebuild()
        }
      }
    })

    result.add(box, BorderLayout.NORTH)
    result
  }

  private def buildSettingsPanel: JPanel =
  {
    val labelx = new JLabel("<html>Width: <br/>(#Intersections)</html>")
    val labely = new JLabel("<html>Hight: <br/>(#Intersections)</html>")
    val labeln = new JLabel("Name ")

    spinnerx.addChangeListener(new ChangeListener
    {
      def stateChanged(e: ChangeEvent)
      {
        curr_width = spinnerx.getModel.asInstanceOf[SpinnerNumberModel].getNumber.intValue()
        rebuild()
      }
    })

    spinnery.addChangeListener(new ChangeListener
    {
      def stateChanged(e: ChangeEvent)
      {
        curr_height = spinnery.getModel.asInstanceOf[SpinnerNumberModel].getNumber.intValue()
        rebuild()
      }
    })

    val edit = new JPanel(new GridLayout(4, 2, 5, 10))
    edit.add(labelx)
    edit.add(spinnerx)
    edit.add(labely)
    edit.add(spinnery)
    edit.add(labeln)
    edit.add(name)
    edit.add(numTokensLabel)

    val result = new JPanel(new BorderLayout())
    result.add(edit, BorderLayout.NORTH)

    val okbtn = new JButton("Save")
    okbtn.addActionListener(new ActionListener
    {
      def actionPerformed(e: ActionEvent)
      {
        var filename = name.getText
        val f = new File("maps/" + filename + ".maze")
        if (!f.isFile) {
          IOUtils.writeToFile(f.getCanonicalPath, model.toJson.prettyPrint)
          Dialogs.info("Successfully written to file.")
        }
        else {
          Dialogs.info("File exists already! Choose another one.")
          IOUtils.letUserChooseFile(f.getCanonicalPath) match {
            case None => Dialogs.info("Aborted. No file was written.")
            case Some(p) =>
              var n = p
              if (!p.endsWith(".maze")) n = n + ".maze"
              IOUtils.writeToFile(n, model.toJson.prettyPrint)
              Dialogs.info("Successfully written to file.")
              filename = new File(n).getName.replace(".maze", "")
          }
        }
        MapController.mazePool +(filename, model)
      }
    })

    result.add(okbtn, BorderLayout.SOUTH)
    result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
    result
  }

  private def rebuild()
  {
    invalidate()
    remove(content)
    content = new JScrollPane(buildMazePanel())
    add(content, BorderLayout.CENTER)
    validate()
  }

  private def buildMazePanel(): JPanel =
  {
    if (model == null || curr_height != model.height || curr_width != model.width)
      model = Maze.empty(curr_width, curr_height)
    numTokensLabel.setText("" + model.getNumberOfToken)
    model.addObserver(numTokensLabel)
    new MazeView(model)
  }

  override def receiveUpdate(subject: MazePool)
  {
    val listeners = box.getActionListeners
    box.removeActionListener(listeners(0))
    box.removeAllItems()
    subject.mazeNames.foreach(box.addItem)
    box.addActionListener(listeners(0))
  }
}
