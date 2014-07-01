name := "finatra-travel-api"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra" % "1.5.2",
  "com.github.tomakehurst" % "wiremock" % "1.46" % "test",
  "ch.qos.logback" % "logback-classic" % "1.0.13"
)

parallelExecution in Test := false

resolvers +=
  "Twitter" at "http://maven.twttr.com"
