organization := "io.newsbridge.sample"
name := "akka-http-swagger"
version := "1.0.0"

scalaVersion := "2.12.3"

val akkaVersion = "2.5.4"
val akkaHttpVersion = "10.0.9"

libraryDependencies += "com.typesafe.akka"             %% "akka-http"              % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-parsing"           % akkaHttpVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-http-spray-json"   % akkaHttpVersion

libraryDependencies += "com.typesafe.akka"             %% "akka-actor"             % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-stream"            % akkaVersion
libraryDependencies += "com.typesafe.akka"             %% "akka-slf4j"             % akkaVersion

//libraryDependencies += "ch.megard"                     %% "akka-http-cors"         % "0.2.1"

libraryDependencies += "com.github.swagger-akka-http"  %% "swagger-akka-http"      % "0.10.1"
libraryDependencies += "io.swagger"                    % "swagger-jaxrs"           % "1.5.16"


// ----------------
dependencyOverrides += "com.typesafe.akka"             %% "akka-stream"      % akkaVersion
dependencyOverrides += "com.typesafe.akka"             %% "akka-actor"       % akkaVersion