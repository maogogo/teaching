package com.maogogo.teaching.rpc.modules

import com.twitter.inject.TwitterModule
import com.maogogo.teaching.common.modules._
import com.maogogo.teaching.thrift._
import com.maogogo.teaching.rpc.oauth2._

object ServicesModule extends TwitterModule with ConfigModule with SimpleMySqlClientModule {

  override def configure: Unit = {
    bindSingleton[OAuth2ServiceDao]
    bindSingleton[OAuth2Service.FutureIface].to[OAuth2ServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"oauth2" -> injector.instance[OAuth2Service.FutureIface]
  )

}