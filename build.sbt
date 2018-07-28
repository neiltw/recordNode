name := "recordNode"
 
version := "1.0" 
      
lazy val `recordnode` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
//  jdbc ,
  ehcache , ws , specs2 % Test , guice ,filters,
  "com.typesafe.play" %% "play-slick" % "3.0.0" ,
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.typesafe.play" %% "play-json" % "2.6.8"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.23"