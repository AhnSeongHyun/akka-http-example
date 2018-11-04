name := "akka-http-example"

version := "0.1"

scalaVersion := "2.12.7"


libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"   % "10.1.5",
    "com.typesafe.akka" %% "akka-stream" % "2.5.12",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5"
)
