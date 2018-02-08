package fr.sysf.sample

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive0, Directives, Route}

trait CorsSupport extends Directives {

  // Wrap the Route with this method to enable adding of CORS headers
  def corsHandler(r: Route): Route = (headerValueByName("origin") | provide("*")) { origin =>
    addAccessControlHeaders(origin)(preflightRequestHandler ~ r)
  }

  //this handles preflight OPTIONS requests.
  private val preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders(origin: String): Directive0 = {
    respondWithHeaders(List(
      Some(origin).filterNot(_ == "*").map(`Access-Control-Allow-Origin`(_)).getOrElse(`Access-Control-Allow-Origin`.*),
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
    ))
  }
}
