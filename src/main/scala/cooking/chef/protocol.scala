package cooking.chef

import akka.actor.typed.ActorRef
import cooking.manager.Reply

sealed trait ChefMsg
//case object AreYouDone extends ChefMsg
final case class AreYouDone(
  replyTo: ActorRef[Reply] // for typed
) extends ChefMsg

sealed trait Food extends ChefMsg { def servings: Int }
final case class Ingredients(servings: Int) extends Food
protected[chef] case class CookedFood(servings: Int) extends Food
protected[chef] case class BurntFood(servings: Int) extends Food
