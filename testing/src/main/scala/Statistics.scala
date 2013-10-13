import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Statistics(path:Seq[Node]) {

  def getTurns():Int = {

    val directions = new ListBuffer[Int]()

    //Constructing a List with the Directions,where 1 is north; 2 is east; 3 is south; and 4 is west
    for(Seq(a,b) <- path.sliding(2)){
      // B is in the south of A
      if (a.x + 1 == b.x) directions += 3
      // B is in the north of A
      if (b.x + 1 == a.x) directions += 1
      // B is in the east of A
      if (a.y + 1 == b.y) directions += 2
      // B is in the west of A
      if (b.y + 1 == a.y) directions += 4
    }

    val turns = for(List(a,b) <- directions.toList.sliding(2)) yield math.abs(a-b)

    turns.sum
  }

  def getVisitedNodes() : Int = {
    path.size
  }
}
