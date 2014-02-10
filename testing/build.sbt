import AssemblyKeys._

assemblySettings

organization  := "tud.robolab"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions += "-g:none"

compileOrder in Test := CompileOrder.JavaThenScala

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

publishArtifact in (Test, packageBin) := true

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2.0",
  "io.spray"            %   "spray-routing" % "1.2.0",
  "io.spray"            %   "spray-client"  % "1.2.0",
  "io.spray"            %   "spray-util"    % "1.2.0",
  "io.spray"            %%  "spray-json"    % "1.2.5",
  "io.spray"            %   "spray-testkit" % "1.2.0",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.3",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.3",
  "org.scalatest"       %   "scalatest_2.10" % "2.0",
  "org.jgrapht"         %   "jgrapht-jdk1.5" % "0.7.3"
)

seq(Revolver.settings: _*)

mainClass in assembly := Some("Main")

jarName in assembly := "RobolabSimTest.jar"

test in assembly := {}
