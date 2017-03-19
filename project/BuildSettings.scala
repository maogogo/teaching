import sbt._
import Keys._
import com.typesafe.sbt.packager.Keys._
import com.twitter.scrooge.ScroogeSBT
import com.twitter.scrooge.ScroogeSBT.autoImport._
import com.typesafe.sbt.SbtNativePackager._
import BuildEnvPlugin.autoImport._

object BuildSettings {

  lazy val basicSettings = Seq(
    scalaVersion := "2.11.8",
    version := "0.0.1-SNAPSHOT",
    organization := "com.maogogo",
    shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " },
    resolvers ++= Dependencies.repositories
  )

  lazy val mapSettings = Seq(
    maintainer in Linux := "Toan <li.tao@hejinonline.com>",
    packageName in Universal := packageName.value,
    //bashScriptExtraDefines += s"""addJava "-Denv=${sys.props.getOrElse("pkg", default = "dev")}"""",
    bashScriptExtraDefines += s"""addJava "-Xmx14G"""",
    bashScriptExtraDefines += s"""addJava "-Denv=${sys.props.getOrElse("env", "dev")}"""",
    bashScriptExtraDefines += """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml"""",
    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
    mappings in(Universal, packageBin) += file("src/main/resources/logback.xml") -> "conf/logback.xml",
    mappings in(Universal, packageBin) += file("src/main/resources/application.conf") -> "conf/application.conf"
  )
    // mappings in Universal ++= (packageBin in Compile, sourceDirectory) map { (_, src) =>
    //   val logback = src / "main" / "resources" / "logback.xml"
    //   logback -> "conf/logback.xml"
    // },
    // mappings in Universal += {
    //   // logic like this belongs into an AutoPlugin
    //   ((resourceDirectory in Compile).value / "application.conf") -> "conf/application.conf"
    // })

  lazy val publishableSettings = Seq(
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    licenses += "BSD-Style" -> url("http://www.opensource.org/licenses/bsd-license.php"),
    homepage := Some(url("http://t.maogogo.com:8080")),
    publishTo := ({
      val nexus = "http://123.56.183.194:8081/nexus/content/repositories/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "snapshots/")
      else
        Some("releases" at nexus + "releases")
    }),
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"))

  lazy val thriftSettings = Seq(
    //(scroogeThriftSourceFolder in Compile) <<= baseDirectory { _ / "../rbac-thrift" }
    (scroogeThriftSourceFolder in Compile) := baseDirectory.value
  )

}
