package com.maogogo.teaching.rpc

import com.twitter.inject.server.TwitterServer
import com.twitter.util.Await
import com.maogogo.teaching.rpc.modules.ServicesModule
import com.typesafe.config.Config
import java.net.InetSocketAddress
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable

object Main extends TwitterServer {

  implicit val config: Config = ServicesModule.provideConfig
  override val adminPort = flag("admin.port", new InetSocketAddress(config.getInt("admin.port")), "")

  val level: Level = Level.ERROR
  override val levelFlag = flag("log.level", level, "")

  override def modules = Seq(ServicesModule)

  override def postWarmup() {

    val services = ServicesModule.services(injector)
    Await.all(services: _*)

    info(s"${logo}\t${adminPort}\t")
    Await.ready(adminHttpServer)
  }

  lazy val logo = """
    ______    ____  ____  ______
   /_  __/   / __ \/ __ \/ ____/
    / /_____/ /_/ / /_/ / /     
   / /_____/ _, _/ ____/ /___   
  /_/     /_/ |_/_/    \____/   """
}