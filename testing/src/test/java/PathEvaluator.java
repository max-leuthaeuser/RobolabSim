import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Fabian
 * Date: 06.10.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */

public class PathEvaluator {

    private List path;

    public PathEvaluator(List path){
        this.path = path;
    }

    private GraphBuilder getBuilder(){
        return new SimpleGraphBuilder().setList(path).setTokenCount(3);
    }

    public boolean validateOneStepConstraint(){
        UndirectedGraph<Node,DefaultEdge> graph =  this.getBuilder().constructPath();

        boolean result = true;

        for(DefaultEdge dE : graph.edgeSet()){

            Node a = graph.getEdgeSource(dE);
            Node b = graph.getEdgeTarget(dE);

            // x values of a and b should only differ by max. 1; So I calculate the absolute difference
            int changeX = Math.abs(a.x() - b.x());

            // y values of a and b should only differ by max. 1; So I calculate the absolute difference
            int changeY = Math.abs(a.y() - b.y());

            // for a correct path, only one changeX or changeY are allowed to be max. 1. Never both of them (would be a diagonal path)
            int changeXY = changeX + changeY;

            // changeX > 1 means, that you made a step across 2 vertexes --> not allowed
            // same for changeY
            // changeXY > 1 means, that you made a diagonal step --> not allowed
            if (changeX > 1 || changeY > 1 || changeXY > 1){
                result = false;
                break;
            }
        }

        return result;
    }

    public boolean validateOnlyOnLineConstraint(){
        UndirectedGraph<Node,DefaultEdge> graph =  this.getBuilder().constructPath();

        boolean result = true;

        for(DefaultEdge dE : graph.edgeSet()){
            Node a = graph.getEdgeSource(dE);
            Node b = graph.getEdgeTarget(dE);

            if(a.x() + 1 == b.x()){
                //B is in the south of A
                if(a.south() == false || b.north() == false){
                    result = false;
                    break;
                }
            }
            if(b.x() +1 == a.x()){
                //B is in the north of A
                if(a.north() == false || b.south() == false){
                    result = false;
                    break;
                }
            }
            if(a.y() + 1 == b.y()){
                //B is in the east of A
                if(a.east() == false || b.west() == false){
                    result = false;
                    break;
                }
            }
            if(b.y() +1 == a.y()){
                //B is in the west of A
                if(a.west() == false || b.east() == false){
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    public boolean validateMaximumVisitedCount(){
        UndirectedGraph<Node,DefaultEdge> multigraph =  this.getBuilder().asMultigraph().addStartPoint().constructExplorationPhaseGraph();
        UndirectedGraph<Node,DefaultEdge> graph =       this.getBuilder().constructExplorationPhaseGraph();

        boolean result = true;

        for (Node n : graph.vertexSet()){
            int neighbors = graph.degreeOf(n);
            int visited = Math.round(multigraph.degreeOf(n) / 2);
            if (visited > neighbors){
                result = false;
                break;
            }
        }

        return result;

    }

    public boolean validateAllTokensFound(){
        if(this.getBuilder().getLastTokenNode() == null){
            return false;
        }
        return true;
    }

    public boolean validateShortestPath(){
        UndirectedGraph<Node,DefaultEdge> driveHome = this.getBuilder().constructDriveHomePhaseGraph();
        UndirectedGraph<Node,DefaultEdge> knownMaze = this.getBuilder().constructKnownMaze();
        Node lastToken = this.getBuilder().getLastTokenNode();

        Node pseudoLastToken = new Node(lastToken.x(), lastToken.y(),false,false,false,false,false);
        Node pseudoHome = new Node(0, 0,false,false,false,false,false);
        DijkstraShortestPath<Node,DefaultEdge> shortestPath = new DijkstraShortestPath(knownMaze, pseudoLastToken, pseudoHome);
        if(shortestPath.getPathLength() == driveHome.edgeSet().size()){
            return true;
        }
        return false;

    }
}
