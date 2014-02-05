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

package tud.robolab.utils

import java.io.{File, PrintWriter, FileWriter}
import java.util.Calendar
import java.text.SimpleDateFormat
import io.Source._
import javax.swing.JFileChooser

object IOUtils
{
  /**
   * Used for reading/writing to database, files, etc.
   * Code From the book "Beginning Scala"
   * http://www.amazon.com/Beginning-Scala-David-Pollak/dp/1430219890
   */
  def using[A <: {def close()}, B](param: A)
    (f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

  def writeToFile(
    fileName: String,
    data: String)
  {
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }
  }

  def appendToFile(
    fileName: String,
    textData: String)
  {
    using(new FileWriter(fileName, true)) {
      fileWriter =>
        using(new PrintWriter(fileWriter)) {
          printWriter => printWriter.print(textData)
        }
    }
  }

  def createDirectory(dir: File)
  {
    if (!dir.exists && !dir.isDirectory)
      dir.mkdirs()
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
    else Stream.empty)

  def getFileTreeFilter(
    f: File,
    str: String): Array[String] = getFileTree(f).filter(_.getName.endsWith(str))
    .map(_.getName.replaceAll(".maze", "")).toArray

  def readFromFile(f: File): String =
  {
    val src = fromFile(f)
    try {
      src.getLines().mkString("\n")
    }
    finally src match {
      case b: scala.io.BufferedSource => b.close()
    }
  }

  def letUserChooseFile(currentDirectory: String): Option[String] =
  {
    val fc = new JFileChooser(currentDirectory)
    val returnVal = fc.showOpenDialog(null)

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      val file = fc.getSelectedFile
      Option(file.getCanonicalPath)
    } else {
      Option.empty
    }
  }
}