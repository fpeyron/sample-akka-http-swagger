package fr.sysf.sample.contact

import java.time.LocalDate
import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import io.swagger.annotations.ApiModelProperty


object ContactActor {

  case object ContactList

  case class ContactForCreation(
                                 @ApiModelProperty(value = "email", required = true, example = "n.biggy@yopmail.com") email: String,
                                 @ApiModelProperty(value = "first name", required = true, example = "Nelson") firstName: String,
                                 @ApiModelProperty(value = "last name", required = true, example = "Bighetti") lastName: String,
                                 @ApiModelProperty(value = "birth date", example = "1970-01-01") birthDate: Option[LocalDate]
                               ) {
    require(Option(email).exists(isValidEmail), s"email is not correct: $email")
    require(Option(firstName).exists(_.length > 1), s"firstname should be more 2 chars: $firstName")
    require(Option(lastName).exists(_.length > 1), s"lastname should be more 2 chars: $lastName")
    require(birthDate.forall(x => x.isAfter(LocalDate.now.minusYears(100)) && x.isBefore(LocalDate.now.plusYears(18))), s"age should be from 18 to 100: $birthDate")
  }

  case class ContactForUpdate(
                               @ApiModelProperty(value = "first name", required = true, example = "Nelson") firstName: Option[String],
                               @ApiModelProperty(value = "last name", required = true, example = "Bighetti") lastName: Option[String],
                               @ApiModelProperty(value = "email", required = true, example = "n.biggy@yopmail.com") email: Option[String],
                               @ApiModelProperty(value = "birth date", example = "1970-01-01") birthDate: Option[LocalDate]
                             ) {
    require(firstName.forall(_.length > 1), s"firstname should be more 2 chars: $firstName")
    require(lastName.forall(_.length > 1), s"lastname should be more 2 chars: $lastName")
    require(email.forall(isValidEmail), s"email is not correct: $email")
    require(birthDate.forall(x => x.isAfter(LocalDate.now.minusYears(100)) && x.isBefore(LocalDate.now.plusYears(18))), s"age should be from 18 to 100: $birthDate")
  }

  case class ContactForDelete(id: UUID)

  case class ContactResponse(
                              @ApiModelProperty(position = 0, value = "id", required = true, example = "3774598d-9b90-4880-a44e-26febf1b2580") id: UUID,
                              @ApiModelProperty(position = 2, value = "first name", required = true, example = "Nelson") firstName: String,
                              @ApiModelProperty(position = 3, value = "last name", required = true, example = "Bighetti") lastName: String,
                              @ApiModelProperty(position = 1, value = "email", required = true, example = "n.biggy@yopmail.com") email: String,
                              @ApiModelProperty(position = 4, value = "birth date", example = "1970-01-01") birthDate: Option[LocalDate]
                            )


  def isValidEmail(email: String): Boolean = if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email).isEmpty) false else true
}

class ContactActor extends Actor with ActorLogging {

  import fr.sysf.sample.contact.ContactActor._

  var state = Seq.empty[ContactResponse]

  def receive: Receive = {

    case ContactList =>
      sender ! state.sortBy(c => c.lastName + c.firstName)

    case contactForCreation: ContactForCreation =>
      if (state.exists(_.email == contactForCreation.email)) {
        sender ! new RuntimeException(s"Contact already exists with email : ${contactForCreation.email}")
      } else {
        val contact = ContactResponse(
          id = UUID.randomUUID(),
          firstName = contactForCreation.firstName.capitalize,
          lastName = contactForCreation.lastName.toUpperCase,
          email = contactForCreation.email.toLowerCase,
          birthDate = contactForCreation.birthDate
        )
        state = state :+ contact
        sender ! contact
      }

    case (id: String, contactForUpdate: ContactForUpdate) =>
      if (!state.exists(_.id == id)) {
        sender ! new RuntimeException(s"Contact doesn't exist : $id")
      } else {
        val existingContact = state.filter(_.id == id).head
        val contact = ContactResponse(
          id = existingContact.id,
          firstName = contactForUpdate.firstName.map(_.capitalize).getOrElse(existingContact.firstName),
          lastName = contactForUpdate.lastName.map(_.toUpperCase).getOrElse(existingContact.lastName),
          email = contactForUpdate.email.map(_.toLowerCase).getOrElse(existingContact.email),
          birthDate = contactForUpdate.birthDate.orElse(existingContact.birthDate)
        )
        state = state.filterNot(_.id == contact.id) :+ contact
        sender ! contact
      }


    case (ContactForDelete(id: UUID)) =>
      if (!state.exists(_.id == id)) {
        sender ! new RuntimeException(s"Contact doesn't exist : $id")
      } else {
        state = state.filterNot(_.id == id)
        sender ! None
      }
  }
}