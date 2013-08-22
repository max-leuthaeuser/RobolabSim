package tud.robolab.model

import scala.collection.concurrent.TrieMap
import tud.robolab.utils.IOUtils

case class Client(ip: String, var blocked: Boolean = false)

case class Session(client: Client, var maze: Maze, var way: Seq[(Int, Int)]) {
  def addPoint(x: Int, y: Int) {
    way = way :+ (x ,y)
  }

  def latestPosition: (Int, Int) = way.last

  override def equals(o: Any) = o match {
    case that: Session => that.client.ip.equals(this.client.ip)
    case _ => false
  }

  override def hashCode = client.ip.hashCode
}
