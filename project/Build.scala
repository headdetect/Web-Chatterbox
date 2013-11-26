import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "ChatterBox"
    val appVersion      = "1.0.0a Indev"

    val appDependencies = Seq(
	  javaCore, 
	  javaJdbc, 
	  javaEbean,
	  cache,
	  "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
