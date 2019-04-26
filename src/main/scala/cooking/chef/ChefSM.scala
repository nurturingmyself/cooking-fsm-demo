package cooking.chef

import akka.actor.{ ActorLogging, FSM }
import akka.pattern._
import cooking.chef.ChefSM.State
import cooking.manager.Reply
import akka.actor.Status

import scala.concurrent.ExecutionContext

object ChefSM {
  sealed trait State
  final case object Cooking extends State
  final case object Plating extends State
  final case object Done extends State
}

class ChefSM(customers: Int, skill: CookingSkill) extends FSM[State, Data] with ActorLogging {

  import ChefSM._
  implicit val ec: ExecutionContext =
    context.system.dispatcher

  startWith(Cooking, Data(served = 0))

  when(Cooking) {
    case Event(ing @ Ingredients(servings), _) =>
      log.info("Cooking {} servings.", servings)
      pipe(skill.cook(ing)) to self
      stay
    case Event(cooked: CookedFood, _) =>
      self ! cooked
      goto(Plating)
    case Event(BurntFood(servings), _) =>
      log.warning("Burnt {} servings.", servings)
      stay
    case Event(AreYouDone, Data(served)) =>
      sender() ! Reply(served, isDone = false)
      stay
  }

  when(Plating) {
    case Event(CookedFood(servings), data) =>
      log.info("Plating {} servings.", servings)
      val newData = Data(data.served + servings)
      if (newData.served >= customers) {
        log.info("All fed.")
        goto(Done).using(newData)
      } else {
        val remaining = customers - newData.served
        log.info("{} customers still hungry", remaining)
        goto(Cooking).using(newData)
      }
    case Event(_: BurntFood, _) =>
      log.warning("I will not plate this food!")
      goto(Cooking)
    case Event(AreYouDone, Data(served)) =>
      sender() ! Reply(served, isDone = false)
      stay
  }

  when(Done) {
    case Event(AreYouDone, Data(served)) =>
      sender() ! Reply(served, isDone = true)
      stay
  }

  onTransition {
    case Plating -> Cooking =>
      log.info("Back to cooking..")
  }

  whenUnhandled {
    case Event(Status.Failure(cause), data) =>
      log.warning("unhandled error while {} with {} customers served: {}", stateName, data.served, cause)
      stay
  }

  initialize()
}
