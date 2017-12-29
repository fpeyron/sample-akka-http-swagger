package fr.sysf.sample.contact

import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import fr.sysf.sample.{CorsSupport, DefaultJsonFormats}
import fr.sysf.sample.contact.ContactActor.{ContactForCreation, ContactForDelete, ContactList, ContactResponse}
import io.swagger.annotations._

import scala.concurrent.ExecutionContext


@Api(value = "/contacts", produces = MediaType.APPLICATION_JSON)
@Path("/contacts")
class ContactService(contactActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats with CorsSupport {

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout = Timeout(2.seconds)

  implicit val contactResponse = jsonFormat5(ContactResponse)
  implicit val contactForCreation = jsonFormat4(ContactForCreation)

  val route = find ~ create ~ remove


  @ApiOperation(value = "find contacts", notes = "", nickname = "findContacts", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return list of contacts", responseContainer = "Seq", response = classOf[ContactResponse]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def find = path("contacts") {
    get {
      complete {
        (contactActor ? ContactList).mapTo[Seq[ContactResponse]]
      }
    }
  }


  @ApiOperation(value = "create contact", notes = "", nickname = "createContact", httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return contact created", response = classOf[ContactResponse]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "contact to create", required = true, dataTypeClass = classOf[ContactForCreation], paramType = "body")
  ))
  def create = path("contacts") {
    post {
      entity(as[ContactForCreation]) { request =>
        complete {
          (contactActor ? request).mapTo[ContactResponse]
        }
      }
    }
  }

  @Path("/{id}")
  @ApiOperation(value = "delete contact", notes = "", nickname = "deleteContact", httpMethod = "DELETE")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return when contact is delete"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "id of contact to create", required = true, dataType = "string", paramType = "path")
  ))
  def remove = delete {
    path("contacts" / JavaUUID) { id =>
      delete {
        onSuccess(contactActor ? (ContactForDelete(id))) {
          case None => complete(StatusCodes.OK)
          case _: RuntimeException => complete(StatusCodes.NotFound )
        }
      }
    }
  }
}

