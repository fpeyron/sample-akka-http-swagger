import java.time.Instant
import sbt.enablePlugins

organization := "fr.sysf.sample"
name := "sample-akka-http-swagger"

scalaVersion := "2.12.4"

lazy val akkaVersion = "2.5.9"
lazy val akkaHttpVersion = "10.0.11"

libraryDependencies += "com.typesafe.akka"             %% "akka-http"              % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-parsing"           % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-http-spray-json"   % akkaHttpVersion

libraryDependencies += "com.typesafe.akka"             %% "akka-actor"             % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-stream"            % akkaVersion

libraryDependencies += "com.github.swagger-akka-http"  %% "swagger-akka-http"      % "0.10.1"
libraryDependencies += "io.swagger"                    % "swagger-jaxrs"           % "1.5.16"

libraryDependencies += "ch.qos.logback"                % "logback-classic"         % "1.2.+"

// ----------------
dependencyOverrides += "com.typesafe.akka"             %% "akka-stream"            % akkaVersion
dependencyOverrides += "com.typesafe.akka"             %% "akka-actor"             % akkaVersion



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
