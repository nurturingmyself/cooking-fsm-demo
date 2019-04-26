package cooking.chef

import akka.actor.testkit.typed.scaladsl.{ ActorTestKit, BehaviorTestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, FeatureSpecLike, GivenWhenThen, Matchers }

import scala.concurrent.duration._

class ChefTypedSpec extends FeatureSpecLike with GivenWhenThen with Matchers with BeforeAndAfterAll {

  val testKit = ActorTestKit()
  val sys = testKit.system

  override protected def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }

  feature("A chef can cook for customers") {
    ignore("a chef given insufficient ingredients stays cooking") {
      val cookingSkill = DistractedNovice()
      val chefTemplate = new ChefTyped(5, cookingSkill)
      val chef = testKit.spawn(chefTemplate.behavior)

      Given("only an insufficient amount of raw food")
      val ingredients = Ingredients(4)

      When("the hungry person cooks and eats it")
      chef ! ingredients // TODO with probe

      Then("the person should remain hungry")
      sys.scheduler.scheduleOnce(500.millis) {
        // assert TODO
      }(sys.executionContext)

      // not sure
      // testkit.returnedBehavior shouldBe hungryPersonTemplate.hungry(Data(9))
    }
  }

  feature("Hungry -> Cooking") {
    scenario("a chef receives insufficient ingredients and stays cooking") {
      val cookingSkill = DistractedNovice()
      val hungryPersonTemplate = new ChefTyped(5, cookingSkill)
      // This is synchronous testing, so only the first behavior will be executed
      // https://doc.akka.io/docs/akka/current/typed/testing.html#synchronous-behavior-testing
      val testkit = BehaviorTestKit(hungryPersonTemplate.behavior)

      Given("an insufficient amount of ingredients")
      val ingredients = Ingredients(4)

      When("the ingredients is delivered")
      testkit.run(ingredients)

      Then("the chef should be cooking")
      testkit.isAlive shouldBe true
      val messages = testkit.selfInbox().receiveAll()
      messages shouldBe Seq(CookedFood(4))
    }
  }
}
