package cooking.chef

import akka.actor.{Actor, ActorLogging}
import akka.pattern._
import cooking.manager.Reply

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class Chef(customers: Int, skill: CookingSkill)
  extends Actor with ActorLogging {

  implicit val ec: ExecutionContext =
    context.system.dispatcher

  def receive: Receive = cooking(Data(served = 0))

  def cooking(data: Data): Receive = {
    case ing @ Ingredients(servings) =>
      log.info("Cooking {} servings.", servings)
      pipe(skill.cook(ing)) to self
    case cooked: CookedFood =>
      self ! cooked
      context.become(plating(data))
    case BurntFood(servings) =>
      log.warning("Burnt {} servings.", servings)
    case AreYouDone =>
      sender() ! Reply(data.served, isDone = false)
  }

  def plating(data: Data): Receive = {
    case CookedFood(servings) =>
      log.info("Plating servings.", servings)
      val newData = Data(data.served + servings)
      if (newData.served >= customers) {
        log.info("All fed.")
        context.become(done(newData))
      } else {
        val remaining = customers - newData.served
        log.info("{} customers still hungry.", remaining)
        context.become(cooking(newData))
      }
    case _: BurntFood =>
      log.warning("I will not plate this food!")
      context.become(cooking(data))
    case AreYouDone =>
      sender() ! Reply(data.served, isDone = false)
  }

  def done(data: Data): Receive = {
    case AreYouDone =>
      sender() ! Reply(data.served, isDone = true)
  }
}
