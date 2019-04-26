package cooking.manager

import akka.actor.FSM.{ CurrentState, SubscribeTransitionCallBack, Transition, UnsubscribeTransitionCallBack }
import akka.actor.{ Actor, ActorLogging, ActorRef }
import cooking.chef.ChefSM

class ManagerFSM() extends Actor with ActorLogging {
  implicit val ec = context.system.dispatcher

  def receive: Receive = emptyKitchen

  def emptyKitchen: Receive = {
    case Introduce(chef: ActorRef) =>
      chef ! SubscribeTransitionCallBack(context.self)
      context.become(listening(chef))
  }

  def listening(chef: ActorRef): Receive = {
    case CurrentState(_, ChefSM.Done) | Transition(_, _, ChefSM.Done) =>
      log.info("The chef is done for the day!")
      chef ! UnsubscribeTransitionCallBack(context.self)
      context.become(emptyKitchen)
  }
}
