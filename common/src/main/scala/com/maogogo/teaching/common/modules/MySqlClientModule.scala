package com.maogogo.teaching.common.modules

import com.maogogo.teaching.common._
import com.typesafe.config.Config
import com.maogogo.teaching.common.utils.ThreeDesUtil
import com.twitter.finagle.Mysql
import com.twitter.finagle.client.DefaultPool
import com.twitter.util.Duration
import com.twitter.finagle.mysql._

trait MySqlClientModule {

  lazy val namespace = "mysql"

  def getConnections(config: Config, k: String): Seq[MySqlPool] = {
    val partitions = config.getInt(s"${k}.partitions")
    val username = config.getString(s"${k}.username")
    val encrypt = config.getBoolean(s"${k}.encrypt")

    val password = encrypt match {
      case true => ThreeDesUtil.decrypt(config.getString(s"${k}.password"))
      case _ => config.getString(s"${k}.password")
    }

    val hosts = config.getString(s"${k}.host")
    val database = config.getString(s"${k}.database")
    val pool = config.getInt(s"${k}.pool")
    val testing = config.getBoolean(s"${k}.testing")

    (0 until partitions) map { i =>
      val clients = hosts.split(",").map(getConnection(_, username, password, database, pool))
      MySqlPool(partitions, testing, clients)
    } toSeq

  }

  private[this] def getConnection(host: String, username: String, password: String, database: String, pool: Int): TransactionsClient = {
    val client = Mysql.client
      .withCredentials(username, password)
      .configured(DefaultPool.Param(
        low = pool,
        high = Int.MaxValue,
        idleTime = Duration.Top,
        bufferSize = 0,
        maxWaiters = Int.MaxValue))

    client.withDatabase(database).newRichClient(host)
  }

}

case class MySqlPool(
  partitions: Int,
  testing: Boolean = false,
  clients: Seq[TransactionsClient])