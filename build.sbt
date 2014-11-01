name := "chat"

version := "1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "play" % "play_2.3.4" % "2.3.4"
//  "com.typesafe.play" %% "play-json" % "2.3.4"
)
