name := "YahooFinanceHistoryFetcher"

organization := "com.github.fsw0422"

version := "0.1.1"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://blog.sigkill9.com"))

publishTo := Some(if (isSnapshot.value) {
  Opts.resolver.sonatypeSnapshots
} else {
  Opts.resolver.sonatypeStaging
})

publishMavenStyle := true

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

scalaVersion := "2.12.3"

crossScalaVersions := Seq("2.11.11")

val catsVersion = "1.0.0-RC1"
val akkaHttpVersion = "10.0.10"
val monocleVersion = "1.4.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-macros" % catsVersion,
  "org.typelevel" %% "cats-kernel" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "org.typelevel" %% "cats-effect" % "0.5",
  "org.typelevel" %% "cats-effect-laws" % "0.5" % Test,
  "org.typelevel" %% "cats-laws" % catsVersion % Test,
  "org.typelevel" %% "cats-testkit" % catsVersion % Test,
  "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-law" % monocleVersion % Test,
  "io.monix" %% "monix" % "3.0.0-RC2",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.0-M4",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

scalacOptions += "-Ypartial-unification"
scalacOptions in Test ++= Seq("-Yrangepos")
publishArtifact in Test := false
enablePlugins(JmhPlugin)

lazy val YahooFinanceHistoryFetcher = project in file(".")
