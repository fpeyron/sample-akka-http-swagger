package fr.sysf.sample.user

import akka.actor.ActorRef
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import fr.sysf.sample.DefaultJsonFormats
import fr.sysf.sample.hello.HelloActor._
import io.swagger.annotations._
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import spray.json.RootJsonFormat

@Api(value = "/users", produces = MediaType.APPLICATION_JSON)
@Path("/hello")
class UserService(helloActor: ActorRef) extends Directives with DefaultJsonFormats {

  import akka.pattern.ask

  import scala.concurrent.duration._

  implicit val timeout: Timeout = Timeout(2.seconds)

  implicit val greetingFormat: RootJsonFormat[Greeting] = jsonFormat1(Greeting)

  val route: Route = getHello ~ getHelloSegment

  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHello: Route =
    path("hello") {
      get {
        complete {
          (helloActor ? AnonymousHello).mapTo[Greeting]
        }
      }
    }

  @Path("hello/{name}")
  @ApiOperation(value = "Return Hello greeting with person's name", notes = "", nickname = "hello", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Name of person to greet", required = false, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting", response = classOf[Greeting]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHelloSegment: Route =
    path("hello" / Segment) { name =>
      get {
        complete {
          (helloActor ? Hello(name)).mapTo[Greeting]
        }
      }
    }
}

