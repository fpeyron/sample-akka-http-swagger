import sbt.enablePlugins

organization := "io.newsbridge.sample"
name := "sample-akka-http-swagger"
version := "1.0.0"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.8"
val akkaHttpVersion = "10.0.11"

libraryDependencies += "com.typesafe.akka"             %% "akka-http"              % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-parsing"           % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-http-spray-json"   % akkaHttpVersion

libraryDependencies += "com.typesafe.akka"             %% "akka-actor"             % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-stream"            % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-slf4j"             % akkaVersion

libraryDependencies += "com.github.swagger-akka-http"  %% "swagger-akka-http"      % "0.10.1"
libraryDependencies += "io.swagger"                    % "swagger-jaxrs"           % "1.5.16"


// ----------------
dependencyOverrides += "com.typesafe.akka"             %% "akka-stream"            % akkaVersion
dependencyOverrides += "com.typesafe.akka"             %% "akka-actor"             % akkaVersion


// ----------------
// Docker packaging
enablePlugins(DockerPlugin, JavaAppPackaging)

packageName in Docker := name.value
version     in Docker := version.value
maintainer in Docker := "contrib@newsbridge.io"
dockerBaseImage := "openjdk:latest"
dockerExposedPorts := Seq(8080)
dockerUpdateLatest := true
