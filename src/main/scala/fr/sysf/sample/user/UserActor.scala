package fr.sysf.sample.user

import akka.actor.{ActorLogging, Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.{PersistentActor, RecoveryCompleted}

class UserActor(id: String) extends PersistentActor with ActorLogging {

  override val persistenceId: String = s"${UserActor.typeName}-$id"

  override def receiveRecover: Receive = {

    case evt: UserCreatedEvt => applyEvt(evt)

    case evt: UserUpdatedEvt => applyEvt(evt)

    case evt: UserCreditedEvt =>
      userState = userState.map(o => o.copy(
        balance = o.balance + evt.amount
      ))

    case evt: UserDebitedEvt =>
      userState = userState.map(o => o.copy(
        balance = o.balance + evt.amount
      ))

    case RecoveryCompleted =>
      // End of recovery
      log.info(s"actor $persistenceId - RecoveryCompleted : balance=${userState.map(_.balance).orNull}")
  }

  override def receiveCommand: Receive = { // scalastyle:ignore

    // Timeout
    case _: ReceiveTimeout => context.parent ! Passivate(stopMessage = StopCmd(self.path.name))


    // Parent send stop
    case _: StopCmd => context.stop(self)


    case cmd: UserCreateCmd => try {

      persist(UserCreatedEvt(
        id = cmd.id,
        email = cmd.email,
        firstName = cmd.firstName,
        lastName = cmd.lastName,
        profileImage = cmd.profileImage
      )) {
        applyEvt(_)
      }

      if (cmd.profileImage.isDefined) {
        persist(UserCreditedEvt(
          id = cmd.id,
          amount = 100,
          label = "Add profileImage"
        )) {
          applyEvt(_)
        }
      }
    } catch {
      // $COVERAGE-OFF$
      case e: Exception => sender() ! akka.actor.Status.Failure(e)
        log.error("Exception caught: {}", e.getStackTrace.mkString("\n"))
      // $COVERAGE-ON$
    }


    case cmd: UserUpdateCmd => try {

      persist(UserUpdatedEvt(
        id = cmd.id,
        email = cmd.email,
        firstName = cmd.firstName,
        lastName = cmd.lastName,
        profileImage = cmd.profileImage
      )) {
        applyEvt(_)
      }

      if (cmd.profileImage.isDefined && userState.exists(_.profileImage.isEmpty)) {
        persist(UserCreditedEvt(
          id = cmd.id,
          amount = 100,
          label = "Add profileImage"
        )) {
          applyEvt(_)
        }
      }
    } catch {
      // $COVERAGE-OFF$
      case e: Exception => sender() ! akka.actor.Status.Failure(e)
        log.error("Exception caught: {}", e.getStackTrace.mkString("\n"))
      // $COVERAGE-ON$
    }


    case cmd: UserDebitCmd => try {

      persist(UserDebitedEvt(
        id = cmd.id,
        amount = cmd.amount,
        label = "External Debit"
      )) {
        applyEvt(_)
      }
    } catch {
      // $COVERAGE-OFF$
      case e: Exception => sender() ! akka.actor.Status.Failure(e)
        log.error("Exception caught: {}", e.getStackTrace.mkString("\n"))
      // $COVERAGE-ON$
    }

  }


  private var userState: Option[UserState] = None


  private def applyEvt: UserEvt => Unit = {

    case evt: UserCreatedEvt =>
      userState = Some(UserState(
        id = evt.id,
        email = evt.email,
        firstName = evt.firstName,
        lastName = evt.lastName
      ))

    case evt: UserUpdatedEvt =>
      userState = userState.map(o => o.copy(
        email = evt.email.getOrElse(o.email),
        firstName = evt.firstName,
        lastName = evt.lastName
      ))

    case evt: UserCreditedEvt =>
      userState = userState.map(o => o.copy(
        balance = o.balance + evt.amount
      ))

    case evt: UserDebitCmd =>
      userState = userState.map(o => o.copy(
        balance = o.balance - evt.amount
      ))
  }
}


object UserActor {

  val typeName: String = "user"

  def props(id: String): Props = Props(new UserActor(id))

  def extractEntityId: ShardRegion.ExtractEntityId = {
    case msg: StopCmd => (msg.entityId, msg)
    case msg: UserCmd => (s"${msg.id}", msg)
  }

  def extractShardId: ShardRegion.ExtractShardId = {
    case msg: UserCmd => s"$typeName-${math.abs(msg.id.hashCode()) % 100}"
  }

}