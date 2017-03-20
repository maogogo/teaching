package com.maogogo.teaching.rest.oauth2

import javax.inject.Inject
import com.twitter.finagle.oauth2._
import io.finch.oauth2._
import com.maogogo.teaching.thrift._
import io.finch._
import com.maogogo.teaching.rest.OAuthRestful

class OAuth2Endponits @Inject() (val dh: DataHandler[TSession]) extends OAuthRestful {

  def endpoints = postToken

  val postToken: Endpoint[GrantHandlerResult] = post("auth" :: "token" :: issueAccessToken(dh)) {
    ghr: GrantHandlerResult => Ok(ghr)
  }

}