package tud.robolab.view

import scala.swing.Dialog
import scala.swing.Dialog.Result

object Dialogs {
  def confirmation(question: String): Boolean =
    Dialog.showConfirmation(parent = null, title = "Confirmation", message = question) match {
      case Dialog.Result.Ok => true
      case _ => false
    }

  def closeOrBlock(): Result.Value = {
    val options = Seq("Close", "No", "Close and block")
    Dialog.showOptions(parent = null, title = "Confirmation", message = "Close and / or block this connection?", entries = options, initial = 0)
  }

  def addOrBlock(title: String): Result.Value = {
    val options = Seq("Yes", "No", "No and block")
    Dialog.showOptions(parent = null, title = "Confirmation [IP: " + title + "]", message = "Accept and / or block this connection?", entries = options, initial = 0)
  }
}
