name := "play-thrift"

lazy val scalaV = "2.11.8"

publish := {}

licenses in ThisBuild += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

bintrayOrganization in ThisBuild := Some("envisia")

organization in ThisBuild := "de.envisia"

libraryDependencies in ThisBuild ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.9.3" % Provided
)

lazy val `play-thrift-core` = (project in file("play-thrift-core")).settings(
  scalaVersion := scalaV,
  crossScalaVersions := Seq("2.11.8", scalaV)
)

lazy val `play-thrift-runtime` = (project in file("play-thrift-runtime")).settings(
  scalaVersion := scalaV,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play" % "2.5.3" % Provided
  )
).dependsOn(`play-thrift-core`)

lazy val `play-thrift-generator` = (project in file("play-thrift-generator")).settings(
  scalaVersion := scalaV,
  crossScalaVersions := Seq("2.11.8", scalaV),
  libraryDependencies ++= Seq(
    "com.github.spullara.mustache.java" % "compiler" % "0.9.1",
    "com.github.scopt" %% "scopt" % "3.4.0"
  ).++(CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, x)) if x >= 11 =>
      Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4")
    case _ => Nil
  })
).dependsOn(`play-thrift-core`).enablePlugins(SbtTwirl)

//lazy val `play-thrift-sbt-plugin` = (project in file("play-thrift-sbt-plugin")).settings(
//  sbtPlugin := true,
//  scalaVersion := "2.10.6"
//).dependsOn(`play-thrift-core`, `play-thrift-generator`)

//lazy val example = (project in file("example")).settings(
//  scalaVersion := "2.11.8",
//  libraryDependencies ++= Seq(
//    "com.twitter" %% "scrooge-core" % "4.7.0",
//    "com.twitter" %% "finagle-thrift" % "6.34.0"
//  )
//)
