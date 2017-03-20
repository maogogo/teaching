package com.maogogo.teaching.rest.oauth2

import com.maogogo.teaching.rest._
import javax.inject.Inject
import com.maogogo.teaching.thrift._
import com.twitter.finagle.oauth2._
import org.slf4j.LoggerFactory
import com.twitter.util.Future

class OAuth2DataHandler @Inject() (service: OAuth2Service.FutureIface) extends DataHandler[TSession] {

  val log = LoggerFactory.getLogger(getClass)

  def createAccessToken(authInfo: AuthInfo[TSession]): Future[AccessToken] = {
    log.info(s"oauth createAccessToken")
    service.createAccessToken(authInfo).map(_.headOption.get)
  }

  def findAccessToken(token: String): Future[Option[AccessToken]] = {
    log.info(s"oauth findAccessToken")
    service.findAccessToken(token).map(_.headOption)
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[TSession]]] = {
    log.info(s"oauth findAuthInfoByAccessToken")
    service.findAuthInfoByAccessToken(accessToken).map(_.headOption)
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[TSession]]] = {
    ???
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[TSession]]] = {
    ???
  }

  def findClientUser(clientId: String, clientSecret: String, scope: Option[String]): Future[Option[TSession]] = {
    ???
  }

  def findUser(username: String, password: String): Future[Option[TSession]] = {
    log.info(s"oauth findUser")
    service.findUser(username, password).map(_.headOption) handle {
      case e: Throwable =>
        log.error("===>>>>>>>>>>>>>>>>", e)
        throw new Exception(e)
    }
  }

  def getStoredAccessToken(authInfo: AuthInfo[TSession]): Future[Option[AccessToken]] = {
    log.info(s"oauth getStoredAccessToken")
    service.getStoredAccessToken(authInfo).map { x =>

      println("--->>>>>>>token ===>>>" + x.head.createdAt)
      x.headOption

    }
  }

  def refreshAccessToken(authInfo: AuthInfo[TSession], refreshToken: String): Future[AccessToken] = {
    log.info(s"oauth refreshAccessToken")
    ???
  }

  def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    log.info(s"oauth validateClient")
    println("clientid ===.>>..." + clientId)
    println("clientSecret ===.>>..." + clientSecret)

    Future.value(true)
  }

}