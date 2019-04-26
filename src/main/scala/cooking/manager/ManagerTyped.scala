package cooking.manager

import akka.actor.typed.scaladsl.{ Behaviors, TimerScheduler }
import akka.actor.typed.{ ActorRef, Behavior }
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import cooking.chef.{ AreYouDone, ChefMsg }

import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object ManagerTyped extends StrictLogging {
  val emptyKitchen =
    Behaviors.withTimers[ManagerMsg] { timers =>
      Behaviors.receiveMessagePartial {
        case IntroduceTyped(chef) =>
          timers.startPeriodicTimer("pollTimer", Poll, 500 millis)
          managing(timers, chef)
      }
    }

  private def managing(timers: TimerScheduler[ManagerMsg], chef: ActorRef[ChefMsg]): Behavior[ManagerMsg] =
    Behaviors.receivePartial {
      case (ctx, Poll) =>
        implicit val timeout = Timeout(2 seconds)
        ctx.ask[ChefMsg, ManagerMsg](chef)(self => AreYouDone(self)) {
          case Success(reply) =>
            reply
          case Failure(ex) =>
            logger.warn(s"Future failed: $ex")
            UnsuccessfulReply(ex)
        }
        Behaviors.same

      case (_, Reply(served, isDone)) =>
        if (isDone) {
          logger.info(s"The chef is done for the day, all $served customers served!")
          timers.cancel("pollTimer")
          emptyKitchen
        } else {
          logger.info("The chef is not done yet.")
          Behaviors.same
        }
    }
}
