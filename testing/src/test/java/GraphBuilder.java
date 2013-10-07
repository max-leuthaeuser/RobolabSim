import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Subgraph;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Fabian
 * Date: 07.10.13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */

public interface GraphBuilder {
    /* The known Maze contains all explicit & implicit information, which are known by the solution.
	 * It possibly contains nodes, which wasn't visited --> important for evaluating the shortest path back home.
	 */
    public abstract UndirectedGraph constructKnownMaze();

    /* 	The path is the representation of the actually driven course
     */
    public abstract UndirectedGraph constructPath();

    /* If you combine these 2 Graphs to one, it will result in the path Graph
    * So these two, are subgraphs of the actual driven path:
    * 			explorationPhasePath:  	is the path until all tokens was found
    *			driveHomePhasePath: 	is the Path back home, after the last token was found (empty if there are no tokens in the maze)
    *@see GraphBuilder#constructDriveHomePhaseGraph()
    */
    public abstract UndirectedGraph constructExplorationPhaseGraph();

    /* If you combine these 2 Graphs to one, it will result in the path Graph
    * So these two, are subgraphs of the actual driven path:
    * 			explorationPhasePath:  	is the path until all tokens was found
    *			driveHomePhasePath: 	is the Path back home, after the last token was found (empty if there are no tokens in the maze)
    *@see GraphBuilder#constructExplorationPhaseGraph()
    */
    public abstract UndirectedGraph constructDriveHomePhaseGraph();

    public abstract Node getLastTokenNode();

    public abstract GraphBuilder setList(List path);

    public abstract GraphBuilder setTokenCount(int tokenCount);

    public abstract GraphBuilder asMultigraph();

    public abstract GraphBuilder addStartPoint();
}
