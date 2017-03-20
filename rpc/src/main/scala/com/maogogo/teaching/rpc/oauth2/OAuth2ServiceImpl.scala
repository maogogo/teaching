package com.maogogo.teaching.rpc.oauth2

import com.maogogo.teaching.rpc._
import com.maogogo.teaching.thrift._
import com.twitter.util.Future
import com.maogogo.teaching.common.utils.DigistUtil
import javax.inject.Inject
import com.maogogo.teaching.common.utils.PasswordHash

class OAuth2ServiceImpl @Inject() (dao: OAuth2ServiceDao) extends OAuth2Service.FutureIface {

  def createAccessToken(authInfo: TAuthInfo): Future[Seq[TAccessToken]] = {

    val userId = authInfo.session.user.id
    val token = DigistUtil.sha1()

    dao.createAccessToken(userId, token).map {
      case x if x > 0 =>
        Seq(TAccessToken(token, None, None, Some(7200), System.currentTimeMillis()))
      case _ => Seq.empty
    }

  }

  def findAccessToken(token: String): Future[Seq[TAccessToken]] = dao.findAccessToken(token)

  def findAuthInfoByAccessToken(accessToken: TAccessToken): Future[Seq[TAuthInfo]] = {
    for {
      userIdOption <- dao.findUserIdByToken(accessToken.token)
      userOption <- dao.findUserById(userIdOption.getOrElse(""))
    } yield {
      userOption match {
        case Some(user) => Seq(TAuthInfo(session = TSession(user = user.copy(passwordHash = "", salt = "")), ""))
        case _ => Seq.empty
      }
    }
  }

  def findAuthInfoByCode(code: String): Future[Seq[TAuthInfo]] = {
    ???
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Seq[TAuthInfo]] = {
    ???
  }

  def findClientUser(clientId: String, clientSecret: String, scope: String): Future[Seq[TSession]] = {
    ???
  }

  def findUser(username: String, password: String): Future[Seq[TSession]] = {

    val resp = for {
      user <- dao.findUserByName(username)
      session = user.flatMap { u =>
        PasswordHash.checkPasswordBySalt(password, u.passwordHash, Some(u.salt)) match {
          case true => Some(TSession(user = u.copy(passwordHash = "", salt = "")))
          case _ => None
        }
      }
    } yield session

    resp
  }

  def getStoredAccessToken(authInfo: TAuthInfo): Future[Seq[TAccessToken]] =
    dao.findAccessTokenByUserId(authInfo.session.user.id)

  def refreshAccessToken(authInfo: TAuthInfo, refreshToken: String): Future[Seq[TAccessToken]] = {
    ???
  }

  def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    ???
  }

}