package com.maogogo.teaching.rest.modules

import com.twitter.inject.TwitterModule
import com.twitter.finagle.http.filter.Cors
import com.maogogo.teaching.common.modules._

object ServicesModule extends TwitterModule with ConfigModule {

  override def configure: Unit = {
    val cors = new Cors.HttpFilter(policy)

    bindSingleton[Cors.HttpFilter].toInstance(cors)
    //bindSingleton[RootService.FutureIface].toInstance(zookClient[RootService.FutureIface]("root"))

  }

  def endpoints(injector: com.twitter.inject.Injector) = {
//    injector.instance[HelloEndpoints].endpoints :+:
//      injector.instance[EngineEndpoints].endpoints
  }

  def filters(injector: com.twitter.inject.Injector) = {
    injector.instance[Cors.HttpFilter]
  }

  private[this] val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods = _ => Some(Seq("GET", "POST", "PUT", "DELETE", "OPTOPNS")),
    allowsHeaders = _ => Some(Seq("Accept", "Content-Type", "Authorization")) //"Access-Control-Allow-Headers": "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
  )

}