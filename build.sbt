import java.time.Instant
import sbt.enablePlugins

organization := "fr.sysf.sample"
name := "sample-akka-http-swagger"

lazy val akkaVersion = "2.5.13"
lazy val akkaHttpVersion = "10.1.1"
lazy val akkaSwaggerVersion = "0.14.0"
lazy val swaggerVersion = "1.5.18"

// ----------------
// Dependencies
// ----------------
// --- akka
libraryDependencies += "com.typesafe.akka"             %% "akka-actor"                      % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-stream"                     % akkaVersion
// --- akka http
libraryDependencies += "com.typesafe.akka"             %% "akka-http"                       % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-parsing"                    % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-http-spray-json"            % akkaHttpVersion
// --- swagger generator
libraryDependencies += "com.github.swagger-akka-http"   %% "swagger-akka-http"              % akkaSwaggerVersion
libraryDependencies += "io.swagger"                     % "swagger-jaxrs"                   % swaggerVersion


// ----------------
// Run
// ----------------
mainClass in (Compile, run) := Some("fr.sysf.sample.Main")
fork in run := true


// ----------------
// Generate BuildInd
// ----------------
enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](organization, name, version, scalaVersion, sbtVersion, description, "buildTime" -> Instant.now)


// ----------------
// Docker packaging
// ----------------
enablePlugins(DockerPlugin, JavaAppPackaging)

packageName               in Docker := s"danon-${name.value}"
version                   in Docker := version.value
maintainer                in Docker   := "technical support <florent.peyron@ext.betc.com>"
dockerBaseImage            := "openjdk:8u151-jre-alpine"
dockerExposedPorts         := Seq(8080)
dockerUpdateLatest        := true

// ----------------
// ScalaStyle
// ----------------
scalastyleFailOnError := true