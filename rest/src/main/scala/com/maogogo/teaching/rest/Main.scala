package com.maogogo.teaching.rest

import com.twitter.inject.server.TwitterServer
import com.typesafe.config.Config
import java.net.InetSocketAddress
import com.maogogo.teaching.rest.modules.ServicesModule
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable
import com.twitter.util.Await
import com.twitter.finagle.Http
import io.finch._

object Main extends TwitterServer {

  val config: Config = ServicesModule.provideConfig
  override val adminPort = flag("admin.port", new InetSocketAddress(config.getInt("admin.port")), "")

  val level: Level = Level.ERROR
  override val levelFlag = flag("log.level", level, "")

  override def modules = Seq(ServicesModule)

  override def postWarmup() {

    val endpoints = ServicesModule.endpoints(injector).toServiceAs[Application.Json]
    val filters = ServicesModule.filters(injector)

    Http.server.serve(s":${config.getInt("http.port")}", filters andThen endpoints)

    info(s"${logo}\t${adminPort}\t")
    Await.ready(adminHttpServer)
  }

  lazy val logo = """
    ______    ____            __ 
   /_  __/   / __ \___  _____/ /_
    / /_____/ /_/ / _ \/ ___/ __/
   / /_____/ _, _/  __(__  ) /_  
  /_/     /_/ |_|\___/____/\__/  """

}