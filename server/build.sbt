import AssemblyKeys._

assemblySettings

organization := "tud.robolab"

version := "0.2"

scalaVersion := "2.11.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
  Seq(base / "src/main/webapp")
}

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-swing" % "2.11.0-M7",
  "io.spray" % "spray-can" % "1.3.2",
  "io.spray" % "spray-caching" % "1.3.2",
  "io.spray" % "spray-routing" % "1.3.2",
  "io.spray" %% "spray-json" % "1.3.2",
  "io.spray" % "spray-testkit" % "1.3.2",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.8",
  "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.8",
  "org.scalatest" % "scalatest_2.11" % "2.2.3",
  "org.jgrapht" % "jgrapht-jdk1.5" % "0.7.3"
)

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

mainClass in Compile := Some("tud.robolab.Boot")

mainClass in assembly := Some("tud.robolab.Boot")

jarName in assembly := "RobolabSim.jar"

scalacOptions in(Compile, doc) <+= (scalaVersion, scalaInstance) map { (
  scalaVer,
  scalaIn
  ) => "-doc-external-doc:" + scalaIn.libraryJar + "#http://www.scala-lang.org/api/" + scalaVer + "/"
}
