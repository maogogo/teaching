package com.maogogo.teaching.rpc.modules

import com.twitter.inject.TwitterModule
import com.maogogo.teaching.common.modules._

object ServicesModule extends TwitterModule with ConfigModule with SimpleMySqlClientModule {
  
  override def configure: Unit = {
//    bindSingleton[MetaDataCacheAccesser]
//    bindSingleton[MetaServiceDao]
//    bindSingleton[EngineService.FutureIface].to[EngineServiceImpl]
//    bindSingleton[MetaService.FutureIface].to[MetaServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
//    s"meta" -> injector.instance[MetaService.FutureIface],
//    s"engine" -> injector.instance[EngineService.FutureIface]
  )
  
  
}