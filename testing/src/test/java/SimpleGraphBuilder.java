import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Fabian
 * Date: 07.10.13
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class SimpleGraphBuilder implements GraphBuilder {
    private List<Node> path;
    private int tokenCount = 3;
    private boolean asMultigraph = false;
    private boolean addPseudoStartPoint = false;

    public SimpleGraphBuilder(){
    }

    /* (non-Javadoc)
     * @see GraphBuilder#constructKnownMaze()
     */
    public UndirectedGraph constructKnownMaze() {
        if(path == null){
            throw new RuntimeException("You have to set the Path, for constructing the Path Graph!");
        }

        UndirectedGraph<Node,DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);

        for(Node n : path){
            Node currentNode =  new Node(n.x(), n.y(), false, false, false, false, false);
            graph.addVertex(currentNode);
            if(n.north()) {
                Node northNode = new Node(n.x()-1,n.y(),false,false,false,false,false);
                graph.addVertex(northNode);
                graph.addEdge(currentNode,northNode);
            }
            if(n.south()){
                Node southNode = new Node(n.x()+1,n.y(),false,false,false,false,false);
                graph.addVertex(southNode);
                graph.addEdge(currentNode,southNode);
            }
            if(n.east()) {
                Node eastNode = new Node(n.x(),n.y()+1,false,false,false,false,false);
                graph.addVertex(eastNode);
                graph.addEdge(currentNode,eastNode);
            }
            if(n.west()) {
                Node westNode = new Node(n.x(),n.y()-1,false,false,false,false,false);
                graph.addVertex(westNode);
                graph.addEdge(currentNode,westNode);
            }
        }

        return graph;
    }

    /* (non-Javadoc)
     * @see GraphBuilder#constructPath()
     */
    public UndirectedGraph<Node,DefaultEdge> constructPath() {
        if(path == null){
            throw new RuntimeException("You have to set the Path, for constructing the Path Graph!");
        }

        UndirectedGraph<Node,DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);
        Node lastNode = null;

        for(Node n : path){
             graph.addVertex(n);
             if(lastNode != null) graph.addEdge(lastNode, n);
             lastNode = n;
        }

        return graph;

    }

    /* (non-Javadoc)
     * @see GraphBuilder#constructExplorationPhaseGraph()
     */
    public UndirectedGraph constructExplorationPhaseGraph() {
        if(path == null){
            throw new RuntimeException("You have to set the Path, for constructing the Path Graph!");
        }


        UndirectedGraph<Node, DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);
        if(asMultigraph){
            // a multigraph allows multiple edges between two vertexes, so we can extrapolate the visited count
            graph = new Multigraph(DefaultEdge.class);
        }

        Node lastNode = null;
        int foundTokens = 0;

        if(addPseudoStartPoint){
            Node pseudoStartPoint = new Node(-1,-1,false,false,false,false,false);
            graph.addVertex(pseudoStartPoint);
            lastNode = pseudoStartPoint;
        }

        for(Node n : path){
            graph.addVertex(n);

            if(lastNode != null) graph.addEdge(lastNode, n);
            lastNode = n;

            if(n.token() == true) foundTokens++;

            if(foundTokens == tokenCount) break;
        }

        return graph;
    }

    /* (non-Javadoc)
     * @see GraphBuilder#constructDriveHomePhaseGraph()
     */
    public UndirectedGraph constructDriveHomePhaseGraph() {
        if(path == null){
            throw new RuntimeException("You have to set the Path, for constructing the Path Graph!");
        }


        UndirectedGraph<Node, DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);
        if(asMultigraph){
            // a multigraph allows multiple edges between two vertexes, so we can extrapolate the visited count
            graph = new Multigraph(DefaultEdge.class);
        }

        Node lastNode = null;
        int foundTokens = 0;


        // constructs the drive home path INCLUSIVE the Node of the last token
        for(Node n : path){
            if(n.token() == true) foundTokens++;

            if(foundTokens == tokenCount){
                graph.addVertex(n);

                if(lastNode != null) graph.addEdge(lastNode, n);
                lastNode = n;
            }



        }

        return graph;

    }

    public Node getLastTokenNode() {
        int foundTokens = 0;

        for(Node n : path){
            if(n.token() == true) foundTokens++;

            if(foundTokens == tokenCount) return n;
        }

        return null;
    }

    public GraphBuilder setList(List path){
        this.path = path;
        return this;
    }

    public GraphBuilder setTokenCount(int tokenCount){
        this.tokenCount = tokenCount;
        return this;

    }

    public GraphBuilder asMultigraph(){
        this.asMultigraph = true;
        return this;

    }

    //is used to get the correct visitedCount for the Node(0,0). Without this pseudo-start-point from outside the maze
    //there would be no incoming edge for it.
    public GraphBuilder addStartPoint(){
        this.addPseudoStartPoint = true;
        return this;
    }

}
