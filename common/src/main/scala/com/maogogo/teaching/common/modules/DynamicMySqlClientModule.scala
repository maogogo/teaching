package com.maogogo.teaching.common.modules

import javax.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton
import javax.inject.Named
import com.typesafe.config.Config

trait DynamicMySqlClientModule extends MySqlClientModule { self =>

  @Provides @Singleton @Named("connections")
  def provideDynamicConnections(implicit config: Config): Map[String, Seq[MySqlPool]] = {
    config.getString(s"${namespace}.dynamic").split(",").map { name =>
      name -> getConnections(config, s"${namespace}.${name}")
    } toMap
  }

  @Provides @Singleton @Named("dynamic")
  def provideDynamic(@Inject() config: Config): Seq[String] =
    config.getString(s"${namespace}.dynamic").split(",")

}