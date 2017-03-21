package com.maogogo.teaching.rest.hello

import io.finch._
import com.maogogo.teaching.rest.OAuthRestful
import com.twitter.finagle.oauth2._
import com.maogogo.teaching.thrift._
import javax.inject.Inject
import com.maogogo.teaching.rest.oauth2.OAuth2DataHandler

class HelloEndpoints @Inject() (val dh: DataHandler[TSession]) extends OAuthRestful {

  def endpoints = hello :+: testauth

  val hello: Endpoint[String] = get("hello") {
    //service.hello("Toan").map(Ok)

    Ok("Hello")
  }

  val testauth: Endpoint[String] = get("testauth" :: auth) {
    //service.hello("Toan").map(Ok)
    auth: AuthInfo[TSession] =>
      Ok("Hello")
  }

}