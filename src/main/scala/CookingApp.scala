import akka.actor.{ ActorSystem, Props }
import cooking.chef._
import cooking.manager.{ Introduce, Manager, ManagerFSM }

import scala.concurrent.duration._

object CookingApp extends App {
  val system = ActorSystem()

  // Burns food when cooking ingredients with servings over 5
  val cookingSkill = DistractedNovice()

  val mode = 2
  val (chef, manager) = mode match {
    case 1 => // untyped
      val chefProps = Props(new Chef(customers = 5, cookingSkill))
      val managerProps = Props(new Manager())

      val chef = system.actorOf(chefProps, "chef")
      val manager = system.actorOf(managerProps, "manager")
      (chef, manager)

    case 2 => // untyped FSM
      val chefProps = Props(new ChefSM(customers = 5, cookingSkill))
      val managerProps = Props(new ManagerFSM())

      val chef = system.actorOf(chefProps, "chef")
      val manager = system.actorOf(managerProps, "manager")
      (chef, manager)
  }

  manager ! Introduce(chef)

  chef ! Ingredients(servings = 9) // burnt
  system.scheduler.scheduleOnce(1 second) {
    chef ! Ingredients(servings = 3)
    chef ! Ingredients(servings = 2)
  }(system.dispatcher)
}
