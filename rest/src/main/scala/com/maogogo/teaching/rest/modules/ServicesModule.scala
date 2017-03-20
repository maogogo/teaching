package com.maogogo.teaching.rest.modules

import com.twitter.inject.TwitterModule
import com.twitter.finagle.http.filter.Cors
import com.maogogo.teaching.common.modules._
import com.maogogo.teaching.rest.hello.HelloEndpoints
import com.maogogo.teaching.rest.oauth2.OAuth2Endponits
import com.maogogo.teaching.thrift._
import com.maogogo.teaching.rest.oauth2.OAuth2DataHandler
import com.google.inject.Provides
import com.google.inject.Singleton
import com.twitter.finagle.oauth2.DataHandler
import com.maogogo.teaching.rest.filters.ExceptionsFilter

object ServicesModule extends TwitterModule with ConfigModule {

  override def configure: Unit = {

    //bindSingleton[Cors.HttpFilter].toInstance(cors)
    bindSingleton[DataHandler[TSession]].to[OAuth2DataHandler]
    bindSingleton[OAuth2Service.FutureIface].toInstance(zookClient[OAuth2Service.FutureIface]("oauth2"))

  }

  def endpoints(injector: com.twitter.inject.Injector) = {
    injector.instance[HelloEndpoints].endpoints :+:
      injector.instance[OAuth2Endponits].endpoints
  }

  def filters(injector: com.twitter.inject.Injector) = {
    injector.instance[Cors.HttpFilter] andThen injector.instance[ExceptionsFilter]
  }

  @Provides @Singleton
  private[this] def provideCorsFilter: Cors.HttpFilter = {
    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST", "PUT", "DELETE", "OPTOPNS")),
      allowsHeaders = _ => Some(Seq("Accept", "Content-Type", "Authorization")) //"Access-Control-Allow-Headers": "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    )
    new Cors.HttpFilter(policy)
  }

}