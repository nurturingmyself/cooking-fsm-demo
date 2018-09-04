package cooking.manager

import akka.actor.{Actor, ActorLogging, ActorRef, Timers}
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import cooking.chef.AreYouDone

import scala.concurrent.duration._

class Manager() extends Actor with ActorLogging with Timers {
  implicit val ec = context.system.dispatcher

  def receive: Receive = emptyKitchen

  def emptyKitchen: Receive = {
    case Introduce(chef: ActorRef) =>
      timers.startPeriodicTimer("pollTimer", Poll, 500 millis)
      context.become(managing(chef))
  }

  def managing(chef: ActorRef): Receive = {
    case Poll =>
      implicit val timeout = Timeout(2 seconds)
      pipe(chef ? AreYouDone) to self
    case Reply(served, isDone) =>
      if (isDone) {
        log.info("The chef is done for the day, all {} customers served!", served)
        timers.cancel("pollTimer")
        context.become(emptyKitchen)
      } else {
        log.info("The chef is not done yet.")
      }
  }
}
