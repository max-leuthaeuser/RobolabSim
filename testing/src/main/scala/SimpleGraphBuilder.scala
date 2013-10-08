import org.jgrapht.graph.{DefaultEdge, Multigraph, SimpleGraph}
import org.jgrapht.UndirectedGraph

class SimpleGraphBuilder(var path: Seq[Node]) extends GraphBuilder[Node, DefaultEdge] {
  private var tokenCount: Int = 3
  private var asMultigraph: Boolean = false
  private var addPseudoStartPoint: Boolean = false

  def constructDriveHomePhaseGraph: UndirectedGraph[Node, DefaultEdge] = {
    assert(path != null)

    var lastNode: Node = null
    var foundTokens = 0

    val graph: UndirectedGraph[Node, DefaultEdge] = asMultigraph match {
      case true => new Multigraph[Node, DefaultEdge](classOf[DefaultEdge])
      case false => new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    }

    // constructs the drive home path INCLUSIVE the Node of the last token
    path.foreach(n => {
      if (n.token) {
        foundTokens += 1
      }
      if (foundTokens == tokenCount) {
        graph.addVertex(n)
        if (lastNode != null) graph.addEdge(lastNode, n)
        lastNode = n
      }
    })

    graph
  }

  def getLastTokenNode: Node = {
    var foundTokens = 0

    path.foreach(n => {
      if (n.token) {
        foundTokens += 1
      }
      if (foundTokens == tokenCount) return n
    })

    null
  }

  def setTokenCount(tokenCount: Int): GraphBuilder[Node, DefaultEdge] = {
    this.tokenCount = tokenCount
    this
  }

  def makeMultiGraph: GraphBuilder[Node, DefaultEdge] = {
    this.asMultigraph = true
    this
  }

  def addStartPoint: GraphBuilder[Node, DefaultEdge] = {
    this.addPseudoStartPoint = true
    this
  }

  def constructExplorationPhaseGraph: UndirectedGraph[Node, DefaultEdge] = {
    assert(path != null)

    var lastNode: Node = null
    var foundTokens = 0

    val graph: UndirectedGraph[Node, DefaultEdge] = asMultigraph match {
      case true => new Multigraph[Node, DefaultEdge](classOf[DefaultEdge])
      case false => new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    }

    if (addPseudoStartPoint) {
      val pseudoStartPoint = new Node(-1, -1)
      graph.addVertex(pseudoStartPoint)
      lastNode = pseudoStartPoint
    }

    path.foreach(n => {
      graph.addVertex(n)
      if (lastNode != null) graph.addEdge(lastNode, n)
      lastNode = n
      if (n.token) {
        foundTokens += 1
      }
      if (foundTokens == tokenCount) return graph
    })

    graph
  }

  def constructKnownMaze: UndirectedGraph[Node, DefaultEdge] = {
    assert(path != null)

    val graph: UndirectedGraph[Node, DefaultEdge] = new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])
    path.foreach(n => {
      val currentNode = new Node(n.x, n.y)
      graph.addVertex(currentNode)

      if (n.north) {
        val northNode: Node = new Node(n.x - 1, n.y)
        graph.addVertex(northNode)
        graph.addEdge(currentNode, northNode)
      }
      if (n.south) {
        val southNode: Node = new Node(n.x + 1, n.y)
        graph.addVertex(southNode)
        graph.addEdge(currentNode, southNode)
      }
      if (n.east) {
        val eastNode: Node = new Node(n.x, n.y + 1)
        graph.addVertex(eastNode)
        graph.addEdge(currentNode, eastNode)
      }
      if (n.west) {
        val westNode: Node = new Node(n.x, n.y - 1)
        graph.addVertex(westNode)
        graph.addEdge(currentNode, westNode)
      }
    })

    graph
  }

  def constructPath: UndirectedGraph[Node, DefaultEdge] = {
    assert(path != null)

    val graph: UndirectedGraph[Node, DefaultEdge] = new SimpleGraph[Node, DefaultEdge](classOf[DefaultEdge])

    var lastNode: Node = null
    path.foreach(n => {
      graph.addVertex(n)
      if (lastNode != null) graph.addEdge(lastNode, n)
      lastNode = n
    })

    graph
  }
}
