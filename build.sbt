scalaVersion := "2.12.3"

name := "YahooFinanceHistoryFetcher"

version := "0.1.0"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://blog.sigkill9.com"))

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) {
    Some("snapshots" at nexus + "content/repositories/snapshots")
  } else {
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
}

scmInfo := Some(
  ScmInfo(
    url("https://github.com/fsw0422/YahooFinanceHistoryFetcher"),
    "scm:git@github.com:fsw0422/YahooFinanceHistoryFetcher.git"
  )
)

developers := List(
  Developer(
    id = "fsw0422",
    name = "Kevin Kwon",
    email = "fsw0422@gmail.com",
    url = url("http://blog.sigkill9.com")
  )
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.9.0",
  "com.google.inject" % "guice" % "4.1.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "org.specs2" %% "specs2-core" % "3.9.4" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
publishArtifact in Test := false
fork in Test := true

lazy val YahooFinanceHistoryFetcher = project in file(".")
