name := "cooking-fsm-demo"

version := "1.0"

scalaVersion := "2.12.6"
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
cancelable in Global := true

lazy val akkaVersion = "2.5.16"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.5.16" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
