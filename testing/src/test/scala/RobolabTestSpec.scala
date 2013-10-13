import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers

class RobolabTestSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers {
  def fixture =
    new {
      val client = new RoblabSimClient("localhost", 8080)
      val tokenCount = client.getNumberOfTokens
      val evaluator = new PathEvaluator(client.getPath.nodes).setTokenCount(tokenCount)
      val historyEvaluator = new PathEvaluator(client.getHistory.nodes)
    }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")

  feature("something, which could be a proper solution, should happen"){

    scenario("No empty solutions are allowed") {
      fixture.client.getPath.nodes.size should not be 0
    }

    ignore("No invalid tokens are allowed (if failed, you drove out of bounds of the maze, in most cases)") {
     fixture.historyEvaluator.validateHistory should not be false
    }

  }

  feature("for a correct solution, some constraints must be fulfilled"){

    scenario("you have to move step by step & you aren't permitted to drive diagonals"){
      fixture.evaluator.validateOneStepConstraint should not be false
    }

    scenario("You should only move on lines") {
      fixture.evaluator.validateOnlyOnLineConstraint should not be false
    }

    scenario("You should not visit a node more often then is has neighbors. (Only until you found the tokens!)") {
      fixture.evaluator.validateMaximumVisitedCount should not be false
    }

  }

  feature("robot explores a maze until all tokens was found, or if there aren't any tokens, the robot should explore the entire maze") {

    scenario("find all tokens if they exist") {
      When("There are tokens")
        fixture.tokenCount should not be 0
      Then("You have to find them all")
        fixture.evaluator.foundUniqueTokens shouldEqual fixture.tokenCount
    }

    scenario("if no token exists you algorithm should terminate after the entire maze is explored") {
      When("There are no tokens")
        fixture.tokenCount shouldEqual 0
      Then("You can not find any of them")
        fixture.evaluator.foundUniqueTokens shouldEqual 0
      And("You should explore the complete maze")
        fixture.evaluator.validateCompleteMazeIsExplored should not be false
      And("Your solution should terminate after the last node")
        fixture.evaluator.validateTerminatedAfterWholeMazeIsExplored should not be false
    }

  }

  feature("robot have to find the shortest path between two nodes, for driving back to start position on a optimal path"){

    scenario("You should drive back the shortest familiar path") {
      When("There are Tokens")
        fixture.tokenCount should not be 0
      Then("You have to drive back to the start node after found the last one")
        fixture.evaluator.validateShortestPath should not be false
    }

  }

  info("Here are some statistics of your course:")
  val stats = new Statistics(fixture.client.getPath.nodes)
  val magicVisitedNodesNumber = 10;
  val magicTurnsNumber = 6;
  info("You visited " + stats.getVisitedNodes() + " Nodes")
  info("You made " + stats.getTurns() + " Turns")
  val score = (stats.getVisitedNodes()*magicVisitedNodesNumber)+(stats.getTurns()*magicTurnsNumber)
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
