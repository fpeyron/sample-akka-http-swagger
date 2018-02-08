scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
//  "-Xfatal-warnings",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-feature",
  "-language:_"
)