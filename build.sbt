val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    organization := "org.hyperdiary",
    name := "solid-client-scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.9.2",
      "org.apache.jena" % "jena-core" % "4.10.0",
      "org.apache.jena" % "jena-arq" % "4.10.0",
      "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.9.2",
      "com.softwaremill.sttp.client3" %% "circe" % "3.9.2",
      "com.github.pureconfig" %% "pureconfig-core" % "0.17.6",
      "io.circe" %% "circe-core" % "0.14.5",
      "io.circe" %% "circe-generic" % "0.14.5",
      "io.circe" %% "circe-parser" % "0.14.5",
      "org.bitbucket.b_c" % "jose4j" % "0.9.5",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
