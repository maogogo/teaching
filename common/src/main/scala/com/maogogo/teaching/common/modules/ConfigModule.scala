package com.maogogo.teaching.common.modules

import com.google.inject._
import com.typesafe.config._
import java.io.File
import com.twitter.finagle.ThriftMux
import com.twitter.scrooge.ThriftService

trait ConfigModule {

  /**
   *
   */
  def zookServer(implicit config: Config) = (s: String) => {
    val label = s"rpc.server.${s}"
    s"zk!${config.getString(label)}"
  }

  def zookClient[T: Manifest](s: String)(implicit config: Config) = {
    provideClient(config.getString(s"rpc.client.${s}"))
  }

  def provideClient[T: Manifest](path: String) = ThriftMux.client.newIface[T](s"zk2!${path}")

  def provideServices(injector: com.twitter.inject.Injector): Map[String, ThriftService] = ???

  def services(injector: com.twitter.inject.Injector)(implicit config: Config) = {
    val rand = new scala.util.Random

    provideServices(injector).map { kv =>
      val (name, service) = kv
      val anounce = s"${zookServer(config)(name)}!" + Math.abs(rand.nextInt)
      ThriftMux.server.serveIface(s":*", service).announce(anounce)
    }.toSeq
  }

  @Provides @Singleton
  implicit def provideConfig: Config = {
    val env = {
      val envProperty = System.getProperty("env")
      val envSystem = if (envProperty == null) System.getenv("env") else envProperty
      if (envSystem == null) "dev" else envSystem
    }

    val path = ConfigFactory.load.getString(env)
    println(s"loading path: ${path} by env: ${env}")
    ConfigFactory parseFile (new File(path)) resolve
  }

}