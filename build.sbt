val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "solid-client-scala",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.9.2",
      "org.apache.jena" % "jena-core" % "4.10.0",
      "org.apache.jena" % "jena-arq" % "4.10.0",
      "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.9.2",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
