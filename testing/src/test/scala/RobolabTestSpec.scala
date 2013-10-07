import scala.collection.JavaConverters.seqAsJavaListConverter
import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers

class RobolabTestSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers {
  def fixture =
    new {
      val client = new RoblabSimClient("localhost", 8080)
      val evaluator = new PathEvaluator(client.getPath().nodes.asJava)
    }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")


  feature("Robot explores a maze") {
    scenario("No empty solutions are allowed") {
      fixture.client.getPath().nodes.size should not be 0
    }

    scenario("You should make only 1 step after another & no diagonals"){
      fixture.evaluator.validateOneStepConstraint() should not be false
    }

    scenario("You should only move on lines"){
      fixture.evaluator.validateOnlyOnLineConstraint() should not be false
    }

    scenario("You should not visit a node more often then is has neighbors. (Only until you found the tokens!)"){
      fixture.evaluator.validateMaximumVisitedCount() should not be false
    }

    scenario("You should drive back the shortest familiar path"){
      fixture.evaluator.validateShortestPath() should not be false
    }


  }
  
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
