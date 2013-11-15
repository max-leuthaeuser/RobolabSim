import org.scalatest.{FunSuite, GivenWhenThen}
import org.scalatest.matchers.ShouldMatchers

class RobolabTestSpec extends FunSuite with GivenWhenThen with ShouldMatchers {
  def fixture =
    new {
      val client = new RoblabSimClient("141.30.61.87", 8080)
      val tokenCount = client.getNumberOfTokens
      val evaluator = new PathEvaluator(client.getPath.nodes).setTokenCount(tokenCount)
      val historyEvaluator = new PathEvaluator(client.getHistory.nodes)
    }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")

  test("No empty solutions are allowed") {
    fixture.client.getPath.nodes.size should not be 0
  }

  test("No invalid tokens are allowed (if failed, you drove out of bounds of the maze, in most cases)") {
    fixture.historyEvaluator.validateHistory should not be false
  }

  test("you have to move step by step & you aren't permitted to drive diagonals") {
    fixture.evaluator.validateOneStepConstraint should not be false
  }

  test("You should only move on lines") {
    fixture.evaluator.validateOnlyOnLineConstraint should not be false
  }

  test("You should not visit a node more often then is has neighbors. (Only until you found the tokens!)") {
    fixture.evaluator.validateMaximumVisitedCount should not be false
  }

  test("find all tokens if they exist. if there are no tokens, you can't find any of them ;)") {
    fixture.evaluator.foundUniqueTokens shouldEqual fixture.tokenCount
  }

  if (fixture.tokenCount == 0) {
    test("if no token exists you should explore the complete maze") {
      fixture.evaluator.validateCompleteMazeIsExplored should not be false
    }

    test("if no token exists you algorithm should terminate after the entire maze is explored") {
      fixture.evaluator.validateTerminatedAfterWholeMazeIsExplored should not be false
    }

    test("When you explore the maze, you should not head back a known path if there are unknown ones directly available") {
      fixture.evaluator.validateIfThereIsADirectUnknownPathDriveIt should not be false
    }
  }

  if (fixture.tokenCount != 0) {
    test("if there are tokens, robot have to find the shortest path between two nodes, for driving back to start position on a optimal path") {
      fixture.evaluator.validateShortestPath should not be false
    }
  }


  info("Here are some statistics of your course:")
  val stats = new Statistics(fixture.client.getPath.nodes)
  val magicVisitedNodesNumber = 10
  val magicTurnsNumber = 6
  val turns = stats.getTurns
  val Lturns = stats.getLTurns
  val Uturns = stats.getUTurns
  val nodes = stats.getVisitedNodes
  info("You visited %s nodes".format(nodes))
  info("You made %s turns".format(turns))
  info("\t%s of them are 90 degree turns".format(Lturns))
  info("\t%s of them are 180 degree turns".format(Uturns))
  val score = (nodes * magicVisitedNodesNumber) + (turns * magicTurnsNumber)
  info("Your score is (the lower the better): " + score)

  /*feature("Setting a new Map") {
    scenario("One should be able to set a new map") {

      Given("A running server")
      // Server needs to be started externally by script

      When("The map is set")
      val path = fixture.client.setMap("tes")

      Then("No error should occur")
    }
  }  */
}
