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

import scala.swing.Dialog
import scala.swing.Dialog.Result

object Dialogs
{
  def confirmation(question: String): Boolean =
    Dialog.showConfirmation(parent = null, title = "Confirmation", message = question) match {
      case Dialog.Result.Ok => true
      case _ => false
    }

  def info(text: String)
  {
    Dialog.showMessage(parent = null, title = "Information", message = text)
  }

  def closeOrBlock(): Result.Value =
  {
    val options = Seq("Close", "No", "Close and block")
    Dialog.showOptions(parent = null, title = "Confirmation", message = "Close and / or block this connection?",
      entries = options, initial = 0)
  }

  def addOrBlock(title: String): Result.Value =
  {
    val options = Seq("Yes", "No", "No and block")
    Dialog.showOptions(parent = null, title = "Confirmation [IP: " + title + "]",
      message = "Accept and / or block this connection?", entries = options, initial = 0)
  }
}
