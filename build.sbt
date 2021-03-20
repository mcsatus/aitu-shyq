name := "aitu-shyq"

version := "1.0.0"

scalaVersion := "2.13.1"


lazy val akkaVersion      = "2.6.8"
lazy val akkaHttpVersion  = "10.2.1"
lazy val json4sVersion    = "3.6.7"
lazy val swaggerVersion   = "2.1.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"      % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"       % akkaVersion,
  "com.typesafe.akka" %% "akka-http"        % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
  //
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "2.0.2",
  //
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.0",
  //
  "de.heikoseeberger" %% "akka-http-circe"      % "1.31.0",
  "io.circe"          %% "circe-core"           % "0.13.0",
  "io.circe"          %% "circe-generic"        % "0.13.0",
  "io.circe"          %% "circe-parser"         % "0.13.0",
  "io.circe"          %% "circe-literal"        % "0.13.0",
  "io.circe"          %% "circe-generic-extras" % "0.13.0",
  "io.circe"          %% "circe-jawn"           % "0.13.0",
  "io.circe"          %% "circe-bson"           % "0.4.0",
  "joda-time"         % "joda-time"             % "2.10.5",
  "ch.qos.logback"    % "logback-classic"       % "1.2.3" % Runtime,
  //

  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.json4s" %% "json4s-native"  % json4sVersion,
  //
  "javax.ws.rs"                  % "javax.ws.rs-api"       % "2.1.1",
  "com.github.swagger-akka-http" %% "swagger-akka-http"    % "2.0.5",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.1.0",
  "io.swagger.core.v3"           % "swagger-core"          % swaggerVersion,
  "io.swagger.core.v3"           % "swagger-annotations"   % swaggerVersion,
  "io.swagger.core.v3"           % "swagger-models"        % swaggerVersion,
  "io.swagger.core.v3"           % "swagger-jaxrs2"        % swaggerVersion,
  "ch.megard"                    %% "akka-http-cors"       % "0.4.1",
  //
  "io.scalaland" %% "chimney" % "0.6.1",
  //
)

enablePlugins(JavaAppPackaging)
