package com.maogogo.teaching.rest

import com.twitter.finagle.oauth2._
import com.maogogo.teaching.thrift._
import io.finch.oauth2._

trait OAuthRestful {

  def dh: DataHandler[TSession]
  val auth = authorize(dh)

}