import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "websocket-chat"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
