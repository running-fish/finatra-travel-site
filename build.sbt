name := "finatra-travel-site"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra" % "1.5.3",
  "org.apache.commons" % "commons-digester3" % "3.2",
  "org.mockito" % "mockito-core" % "1.9.0" % "test",
  "org.jsoup" % "jsoup" % "1.7.3" % "test",
  "com.github.tomakehurst" % "wiremock" % "1.46" % "test",
  "com.jayway.jsonpath" % "json-path" % "0.9.1" % "test",
  "com.jayway.jsonpath" % "json-path-assert" % "0.9.1" % "test"
)

parallelExecution in Test := false

resolvers +=
  "Twitter" at "http://maven.twttr.com"
