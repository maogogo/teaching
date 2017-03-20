package com.maogogo.teaching.common.dao

import com.maogogo.teaching.common._
import javax.inject.Inject
import com.twitter.finagle.mysql._
import javax.inject.Named
import org.slf4j.LoggerFactory
import com.twitter.util._
import com.maogogo.teaching.common.modules.MySqlPool

class SimpleConnectionBuilder(connections: Seq[MySqlPool]) extends ConnectionBuilder {

  /**
   * 这里需要两个参数
   * 1、确定partition
   * 2、确定使用client
   */
  def build[T](partition: Int = 0, serial: Int = 0)(fallback: TransactionsClient => Future[T]): Future[T] = {
    buildConnection(connections, partition, serial)(fallback)
  }

  /**
   * partition = -1 随机
   * serial = 0 取第一个
   */
  def build[T](fallback: TransactionsClient => Future[T]): Future[T] = build(-1, 0)(fallback)

}