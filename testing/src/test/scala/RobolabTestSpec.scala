import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.scalatest.matchers.ShouldMatchers

class RobolabTestSpec extends FeatureSpec with GivenWhenThen with ShouldMatchers {
  def fixture =
    new {
      // val studentSolution = ...
      // val server = ...
      val client = new RoblabSimClient("localhost", 8080)
    }

  info("During the exploration of a maze the robot should")
  info("not cross any node more than once except it is")
  info("a dead end or there is no other unexplored way.")

  feature("Maze exploration") {
    scenario("Robot explores the maze and searches all 3 tokens") {

      Given("a running server")
      // fixture.server.run
      Given("and a running and successfully terminated student solution")
      // fixture.studentSolution.run

      When("the path is returned")
      val path = fixture.client.getPath()

      Then("it is printed")
      println(path)

      Then("and should not be empty")
      path.nodes.size should not be 0
    }
  }
  
  feature("Setting a new Map") {
    scenario("One should be able to set a new map") {

      Given("a running server")
      // fixture.server.run

      When("the map is set")
      val path = fixture.client.setMap("maze")

      Then("no error should occur")
    }
  }
}
