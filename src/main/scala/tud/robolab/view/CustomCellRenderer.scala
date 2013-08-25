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

import javax.swing.{JComponent, ListCellRenderer, JList, DefaultListCellRenderer}
import java.awt.{Color, Component}


class CustomCellRenderer extends ListCellRenderer[String] {
  private val peerRenderer: ListCellRenderer[String] = (new DefaultListCellRenderer).asInstanceOf[ListCellRenderer[String]]
  private val color = new Color(237, 237, 237)

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
