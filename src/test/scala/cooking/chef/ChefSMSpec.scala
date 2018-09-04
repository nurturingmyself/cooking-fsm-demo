package cooking.chef

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FeatureSpecLike, GivenWhenThen}

class ChefSMSpec
  extends TestKit(ActorSystem())
  with FeatureSpecLike
  with GivenWhenThen {

  feature("Testing Untyped FSM actor") {
    scenario("Chef starts in the Plating state and receives BurntFood") {
      Given("Plating state")
      When("receiving BurntFood")
      Then("transitions back to cooking")
      // TODO
    }
  }

}
