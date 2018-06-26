package fr.sysf.sample

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.ClusterSharding
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import buildinfo.BuildInfo
import com.typesafe.config.ConfigFactory
import fr.sysf.sample.contact.{ContactActor, ContactService}
import fr.sysf.sample.hello.{HelloActor, HelloService}
import fr.sysf.sample.swagger.SwaggerDocService
import fr.sysf.sample.user.UserActor

import scala.concurrent.ExecutionContext

object Main extends App with RouteConcatenation {

  // configurations
  val config = ConfigFactory.parseString(
    s"""
       |akka {
       |  loglevel = INFO
       |  stdout-loglevel = INFO
       |
       |  actor {
       |    provider  = "cluster"
       |  }
       |
       |  persistence {
       |    journal {
       |      plugin = "jdbc-journal"
       |      auto-start-journals = ["jdbc-journal"]
       |    }
       |    snapshot-store {
       |      plugin = "jdbc-snapshot-store"
       |      auto-start-snapshot-stores = ["jdbc-snapshot-store"]
       |    }
       |  }
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
  implicit val executionContext: ExecutionContext = system.dispatcher

  // needed for shutdown properly
  sys.addShutdownHook(system.terminate())

  // Start actors
  val hello = system.actorOf(Props[HelloActor])
  val contact = system.actorOf(Props[ContactActor])


  // Start Actor Proxy
  val UserActorProxy: ActorRef = ClusterSharding(system).startProxy(
    typeName = UserActor.typeName,
    extractEntityId = UserActor.extractEntityId,
    extractShardId = UserActor.extractShardId,
    role = None
  )


  // start http services
  val routes = SwaggerDocService.routes ~ new HelloService(hello).route ~ new ContactService(contact).route
  //val bindingFuture = Http().bindAndHandle(routes, address, port)

  // logger
  val logger = Logging(system, getClass)
  logger.info(s"Build version  : ${BuildInfo.version}")
  logger.info(s"Build time     : ${BuildInfo.buildTime}")
  logger.info(s"Cluster id     : ${system.name}")
  logger.info(s"Service http   : http://$address:$port")
  //logger.info(s"Service info   : http://${address}:${port}/info")
  //logger.info(s"Service health : http://${address}:${port}/health")
  logger.info(s"Swagger json   : http://$address:$port/api-docs/swagger.json")
  logger.info(s"Swagger ui     : http://$address:$port/swagger")

  Http().bindAndHandle(routes, address, port)
}
