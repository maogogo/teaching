package com.maogogo.teaching.common.modules

import com.google.inject.Provides
import com.google.inject.Singleton
import javax.inject.Named
import javax.inject.Inject
import com.typesafe.config.Config

trait SimpleMySqlClientModule extends MySqlClientModule { self =>

  @Provides @Singleton @Named("connections")
  def provideConnection(implicit config: Config): Seq[MySqlPool] = {
    getConnections(config, namespace)
  }

}