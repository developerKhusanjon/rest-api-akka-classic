name := "rest-api-akka-classic"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  val akkaVersion = "2.8.5"
  val akkaHttp = "10.5.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core"  % akkaHttp,
    "com.typesafe.akka" %% "akka-http"       % akkaHttp,
    "com.typesafe.play" %% "play-ws-standalone-json"       % "2.1.11",
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.5.6",
    "de.heikoseeberger" %% "akka-http-play-json"   % "1.39.2",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "3.2.18"       % "test"
  )
}
