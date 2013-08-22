package tud.robolab.view

import javax.swing.{JComponent, ListCellRenderer, JList, DefaultListCellRenderer}
import java.awt.{Color, Component}


class CustomCellRenderer extends ListCellRenderer[String] {
  private val peerRenderer: ListCellRenderer[String] = (new DefaultListCellRenderer).asInstanceOf[ListCellRenderer[String]]
  private val color = new Color(237, 237, 237)


  override def getListCellRendererComponent(list: JList[_ <: String], cell: String, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component = {
    val component = peerRenderer.getListCellRendererComponent(list, cell, index, isSelected, cellHasFocus).asInstanceOf[JComponent]
    if (index % 2 == 0)
      component.setBackground(color)
    else
      component.setBackground(Color.white)
    component
  }
}
