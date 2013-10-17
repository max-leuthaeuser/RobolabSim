class Statistics(path: Seq[Node]) {

  def distinguishLoc(a: Node, b: Node): Int = {
    if (a.x + 1 == b.x) return 3
    // B is in the north of A
    if (b.x + 1 == a.x) return 1
    // B is in the east of A
    if (a.y + 1 == b.y) return 2
    // B is in the west of A
    if (b.y + 1 == a.y) return 4
    else 0
  }

  def calcTurns(a: Int, b: Int): Int = math.abs(a - b) match {
    case 3 => 1
    case t => t
  }

  def getTurnsIt: Seq[Int] = path.sliding(2).map {
    case Seq(a, b) => distinguishLoc(a, b)
  }.sliding(2).map {
    case Seq(a, b) => calcTurns(a, b)
  }.toSeq

  def getTurns: Int = getTurnsIt.sum

  def getUTurns: Int = getTurnsIt.count(_ == 2)

  for(n <- getTurnsIt){
    println(n)
  }

  def getLTurns: Int = getTurnsIt.count(_ == 1)

  def getVisitedNodes: Int = path.size
}
