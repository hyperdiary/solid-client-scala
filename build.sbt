val scala3Version = "3.3.1"
val sttpVersion = "3.9.5"
val circeVersion = "0.14.7"

lazy val root = project
  .in(file("."))
  .settings(
    organization := "org.hyperdiary",
    name := "solid-client-scala",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core"            % sttpVersion,
      "com.softwaremill.sttp.client3" %% "okhttp-backend"  % sttpVersion,
      "com.softwaremill.sttp.client3" %% "circe"           % sttpVersion,
      "org.apache.jena"                % "jena-core"       % "4.10.0",
      "org.apache.jena"                % "jena-arq"        % "4.10.0",
      "com.github.pureconfig"         %% "pureconfig-core" % "0.17.6",
      "com.monovore"                  %% "decline"         % "2.4.1",
      "io.circe"                      %% "circe-core"      % circeVersion,
      "io.circe"                      %% "circe-generic"   % circeVersion,
      "io.circe"                      %% "circe-parser"    % circeVersion,
      "org.bitbucket.b_c"              % "jose4j"          % "0.9.5",
      "org.scalameta"                 %% "munit"           % "0.7.29" % Test
    )
  )
