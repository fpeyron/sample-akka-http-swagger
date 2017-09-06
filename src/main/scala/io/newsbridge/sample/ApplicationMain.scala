package io.newsbridge.sample

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import io.newsbridge.sample.contact.{ContactActor, ContactService}
import io.newsbridge.sample.hello.{HelloActor, HelloService}
import io.newsbridge.sample.swagger.SwaggerDocService

import scala.concurrent.ExecutionContextExecutor

object ApplicationMain extends App with RouteConcatenation {

  // configurations
  val config = ConfigFactory.parseString(
    s"""
       |akka {
       |  loglevel = INFO
       |  stdout-loglevel = INFO
       |}
       |app {
       |  http-service {
       |    address = "0.0.0.0"
       |    port = 8080
       |  }
       |}
       """.stripMargin).withFallback(ConfigFactory.load())
  val address = config.getString("app.http-service.address")
  val port = config.getInt("app.http-service.port")

  // needed to run the route
  implicit val system: ActorSystem = ActorSystem("akka-http-sample", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // needed for the future map/flatMap in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // needed for shutdown properly
  sys.addShutdownHook(system.terminate())

  // Start actors
  val hello = system.actorOf(Props[HelloActor])
  val contact = system.actorOf(Props[ContactActor])

  // start http services
  val routes = new HelloService(hello).route ~ new ContactService(contact).route ~ SwaggerDocService.routes
  //val bindingFuture = Http().bindAndHandle(routes, address, port)

  // logger
  val logger = Logging(system, getClass)

  logger.info(s"Server online at http://$address:$port/\n" +
    s"Swagger description http://$address:$port/api-docs/swagger.json\n")

  Http().bindAndHandle(routes, address, port)
}
