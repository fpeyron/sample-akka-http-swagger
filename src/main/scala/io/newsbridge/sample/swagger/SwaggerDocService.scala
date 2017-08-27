package io.newsbridge.sample.swagger

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.newsbridge.sample.CorsSupport
import io.newsbridge.sample.contact.ContactService
import io.newsbridge.sample.hello.HelloService
import io.swagger.models.ExternalDocs
import io.swagger.models.auth.BasicAuthDefinition

object SwaggerDocService extends SwaggerHttpService with CorsSupport {

  override val apiClasses = Set(classOf[HelloService], classOf[ContactService])
  override val info = Info(version = "1.0")
  override val externalDocs = Some(new ExternalDocs("Core Docs", "http://acme.com/docs"))
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}