package cooking.manager

import akka.actor.ActorRef
import akka.actor.typed.{ActorRef => ActorRefTyped}

sealed trait ManagerMsg
final case class Introduce(chef: ActorRef) extends ManagerMsg
final case class IntroduceTyped(
  chef: ActorRefTyped[cooking.chef.ChefMsg] // for typed
) extends ManagerMsg
final case object Poll extends ManagerMsg
final case class Reply(served: Int, isDone: Boolean) extends ManagerMsg

// for typed
final case class UnsuccessfulReply(cause: Throwable) extends ManagerMsg