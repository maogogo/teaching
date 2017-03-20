package com.maogogo.teaching.rest.oauth2

import javax.inject.Inject
import com.twitter.finagle.oauth2._
import io.finch.oauth2._
import com.maogogo.teaching.thrift._
import io.finch._
import com.maogogo.teaching.rest.OAuthRestful

class OAuth2Endponits @Inject() (val dh: DataHandler[TSession]) extends OAuthRestful {

  def endpoints = postToken

  /**
   * header:
   * 	Content-Type: application/x-www-form-urlencoded
   * 	Authorization: basic dGVhY2hpbmc6MjZhNjhlZjgtYTkxNy00Y2RmLWE0NDUtNGE4OTllZDI1NzU3
   *
   * body(x-www-form-urlencoded):
   * 	grant_type: password
   *  username: toan
   *  password: 1
   */
  val postToken: Endpoint[GrantHandlerResult] = post("auth" :: "token" :: issueAccessToken(dh)) {
    ghr: GrantHandlerResult => Ok(ghr)
  }

}