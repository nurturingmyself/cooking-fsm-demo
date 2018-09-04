import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import cooking.chef.{ChefTyped, DistractedNovice, Ingredients}
import cooking.manager.{IntroduceTyped, ManagerTyped}

import scala.concurrent.duration._

object CookingAppTyped extends App {
  val main: Behavior[NotUsed] =
    Behaviors.setup { ctx ⇒
      val manager = ctx.spawn(ManagerTyped.emptyKitchen, "manager")

      // Burns food when cooking ingredients with servings over 5
      val cookingSkill = DistractedNovice()
      val chefTemplate = new ChefTyped(5, cookingSkill)
      val chef = ctx.spawn(chefTemplate.behavior, "chef")

      manager ! IntroduceTyped(chef)

      chef ! Ingredients(9) // burnt
      ctx.system.scheduler.scheduleOnce(1 second) {
        chef ! Ingredients(3)
        chef ! Ingredients(2)
      }(ctx.system.executionContext)

      Behaviors.empty
    }

  val system = ActorSystem(main, "CookingDemo")
}
