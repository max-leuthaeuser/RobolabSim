class Statistics(path: Seq[Node]) {
  def getTurns: Int = path.sliding(2).map {
    case Seq(a, b) => {
      if (a.x + 1 == b.x) 3
      // B is in the north of A
      if (b.x + 1 == a.x) 1
      // B is in the east of A
      if (a.y + 1 == b.y) 2
      // B is in the west of A
      if (b.y + 1 == a.y) 4
      else 0
    }
  }.sliding(2).map {
    case Seq(a, b) => math.abs(a - b)
  }.sum

  def getVisitedNodes: Int = path.size
}
