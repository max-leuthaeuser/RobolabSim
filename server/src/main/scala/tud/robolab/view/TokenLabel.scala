package tud.robolab.view

import java.util.{Observable, Observer}
import javax.swing.JLabel

import tud.robolab.model.Maze

object TokenLabel
{
  private val TEXT = "#Tokens: "

  def apply(): TokenLabel =
  {
    val r = new TokenLabel()
    r.setText(TEXT + 0)
    r
  }
}

class TokenLabel extends JLabel with Observer
{
  override def setText(t: String): Unit =
  {
    super.setText(TokenLabel.TEXT + t)
  }

  override def update(
    o: Observable,
    arg: scala.Any
    ): Unit =
  {
    o match {
      case m: Maze => setText("" + m.getNumberOfToken)
      case _ => // do nothing
    }
  }
}
