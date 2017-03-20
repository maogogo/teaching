package com.maogogo.teaching.rpc.oauth2

import javax.inject.Inject
import javax.inject.Named
import com.maogogo.teaching.common.modules.MySqlPool
import com.maogogo.teaching.common.dao.SimpleConnectionBuilder
import com.twitter.util.Future
import java.util.Date
import com.maogogo.teaching.thrift._
import com.twitter.finagle.mysql.Row

class OAuth2ServiceDao @Inject() (
    @Named("connections") connections: Seq[MySqlPool]
) extends SimpleConnectionBuilder(connections) {

  def createAccessToken(userId: String, token: String): Future[Int] = {
    val sql = s"""INSERT INTO t_oauth_access_tokens (user_id, access_token, expires_in, created_at) 
                    VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE access_token=?, expires_in=?"""

    build {
      _.prepare(sql)(userId, token, 3600 * 2, new Date, token, 3600 * 2).map(executeUpdate)
    }
  }

  def findAccessToken(token: String): Future[Option[TAccessToken]] = {
    val sql = s"select * from t_oauth_access_tokens where access_token=? limit 1"
    build {
      _.prepare(sql)(token).map { result { rowToAccessToken } }
    }
  }

  def findUserIdByToken(token: String): Future[Option[String]] = {
    val sql = s"select * from t_oauth_access_tokens where access_token=? limit 1"
    build {
      _.prepare(sql)(token).map { result { _("user_id").asOptionString } }
    }
  }

  def findUserById(id: String): Future[Option[TUser]] = {
    val sql = s"SELECT * FROM t_oauth_users WHERE user_id=?"
    build {
      _.prepare(sql)(id).map { result { rowToUser } }
    }
  }

  def findUserByName(username: String): Future[Option[TUser]] = {
    val sql = s"SELECT * FROM t_oauth_users WHERE username=? limit 1"
    build {
      _.prepare(sql)(username).map { result { rowToUser } }
    }
  }

  def findAccessTokenByUserId(userId: String): Future[Option[TAccessToken]] = {
    val sql = s"select * from t_oauth_access_tokens where user_id=? limit 1"
    build {
      _.prepare(sql)(userId).map { result { rowToAccessToken } }
    }
  }

  private[this] def rowToAccessToken(row: Row): Option[TAccessToken] =
    Some(TAccessToken(
      token = row("access_token").asString,
      refreshToken = None, scope = None,
      expiresIn = row("expires_in").asOptionLong,
      createdAt = row("created_at").asDatetime
    ))

  private[this] def rowToUser(row: Row): Option[TUser] =
    Some(TUser(
      id = row("user_id").asString,
      username = row("username").asString,
      passwordHash = row("password_hash").asString,
      salt = row("salt").asString
    ))
}