import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers

class RobolabTestSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers {
  def fixture =
    new {
      val client = new RoblabSimClient("localhost", 8080)
    }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")

  feature("Maze exploration") {
    scenario("Robot explores the maze and searches all 3 tokens") {

      Given("A running server")
      // Server needs to be started externally by script
      Given("A running and successfully terminated student solution")
      // user solution  needs to be started externally by script

      When("The path is returned")
      val path = fixture.client.getPath()

      Then("It is printed")
      println(path)

      Then("It should not be empty")
      path.nodes.size should not be 0
    }
  }
  
  feature("Setting a new Map") {
    scenario("One should be able to set a new map") {

      Given("A running server")
      // Server needs to be started externally by script

      When("The map is set")
      val path = fixture.client.setMap("maze")

      Then("No error should occur")
    }
  }
}
