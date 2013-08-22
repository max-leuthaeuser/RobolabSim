package tud.robolab.utils

import java.io.{File, PrintWriter, FileWriter}
import java.util.Calendar
import java.text.SimpleDateFormat
import io.Source._

object IOUtils {
  /**
   * Used for reading/writing to database, files, etc.
   * Code From the book "Beginning Scala"
   * http://www.amazon.com/Beginning-Scala-David-Pollak/dp/1430219890
   */
  def using[A <: {def close()}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

  def writeToFile(fileName: String, data: String) {
    using(new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }
  }

  def appendToFile(fileName: String, textData: String) {
    using(new FileWriter(fileName, true)) {
      fileWriter =>
        using(new PrintWriter(fileWriter)) {
          printWriter => printWriter.print(textData)
        }
    }
  }

  def now: String = {
    val DATE_FORMAT_NOW = "HH:mm:ss:SSS"
    val cal = Calendar.getInstance()
    val sdf = new SimpleDateFormat(DATE_FORMAT_NOW)
    sdf.format(cal.getTime)
  }

  def createDirectory(dir: File) {
    if (!dir.exists && !dir.isDirectory)
      dir.mkdirs()
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
    else Stream.empty)

  def getFileTreeFilter(f: File, str: String): Array[String] = getFileTree(f).filter(_.getName.endsWith(str)).map(_.getName.replaceAll(".maze", "")).toArray

  def readFromFile(f: File): String = {
    val src = fromFile(f)
    try {
      src.getLines().mkString("\n")
    }
    finally src match {
      case b: scala.io.BufferedSource => b.close()
    }
  }
}