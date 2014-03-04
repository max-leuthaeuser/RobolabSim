import AssemblyKeys._

assemblySettings

organization  := "tud.robolab"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

unmanagedResourceDirectories in Compile <++= baseDirectory { base =>
  Seq( base / "src/main/webapp" )
}

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.scala-lang"      %   "scala-swing"   % "2.10.2",
  "io.spray"            %   "spray-can"     % "1.2.0",
  "io.spray"            %   "spray-caching" % "1.2.0",
  "io.spray"            %   "spray-routing" % "1.2.0",
  "io.spray"            %%  "spray-json"    % "1.2.5",
  "io.spray"            %   "spray-testkit" % "1.2.0",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.3",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.3",
  "org.scalatest"       %   "scalatest_2.10" % "2.0",
  "org.jgrapht"         %   "jgrapht-jdk1.5" % "0.7.3"
)

seq(Revolver.settings: _*)

seq(Twirl.settings: _*)

mainClass in Compile := Some("tud.robolab.Boot")

mainClass in assembly := Some("tud.robolab.Boot")

jarName in assembly := "RobolabSim.jar"
