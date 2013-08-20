package tud.robolab.view

import scala.swing.Dialog

object Dialogs {
  def confirmation(question: String): Boolean =
    Dialog.showConfirmation(parent = null, title = "Confirmation", message = question) match {
      case Dialog.Result.Ok => true
      case _ => false
    }
}
