package tud.robolab.model

import spray.json._
import tud.robolab.model.MazeJsonProtocol._
import tud.robolab.utils.IOUtils
import java.io.File
import scala.collection.concurrent.TrieMap

class MazePool extends Subject[MazePool] {
  val pool = TrieMap[String, Maze]()

  IOUtils.createDirectory(new File("maps/"))
  IOUtils.getFileTreeFilter(new File("maps/"), ".maze").foreach(m => {
    pool(m) = IOUtils.readFromFile(new File("maps/" + m + ".maze")).asJson.convertTo[Maze]
  })

  def mazeNames: Iterable[String] = pool.keys

  def apply(name: String): Maze = pool(name)

  def +(name: String, maze: Maze) {
    pool(name) = maze
    notifyObservers()
  }

  def -(name: String) {
    pool.remove(name)
    notifyObservers()
  }
}
