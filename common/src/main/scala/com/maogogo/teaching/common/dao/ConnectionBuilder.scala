package com.maogogo.teaching.common.dao

import com.maogogo.teaching.common._
import javax.inject.Inject
import com.twitter.finagle.mysql._
import javax.inject.Named
import org.slf4j.LoggerFactory
import com.twitter.util._
import com.maogogo.teaching.common.modules.MySqlPool

trait ConnectionBuilder extends BaseDao { self =>

  lazy val log = LoggerFactory.getLogger(getClass)
  lazy val testSQL = "select 1"

  /**
   * 分区connections
   * 分区index
   * connection序号
   * 分区总数
   * 回调函数
   */
  def buildConnection[T](connections: Seq[MySqlPool], partition: Int = 0, serial: Int = 0)(fallback: TransactionsClient => Future[T]): Future[T] = {

    val partitionIndex = (partition > -1 && partition < connections.size) match {
      case true => partition
      case _ => new scala.util.Random().nextInt(connections.size)
    }

    val pool = connections(partitionIndex)

    val serialIndex = (serial > -1 && serial < pool.clients.size) match {
      case true => serial
      case _ => new scala.util.Random().nextInt(pool.clients.size)
    }

    val s = pool.clients(0)
    Futures.flatten(for {
      i <- pool.testing match {
        case true => pool.clients(serialIndex).select(testSQL) { _("1").asInt } handle {
          case t: Throwable =>
            log.error(s"sql[${testSQL}] detect failed cause: ", t)
            Seq(-1)
        }
        case _ => Future.value(Seq(serialIndex))
      }
      j = i.headOption match {
        case Some(x) if x != -1 => x
        case _ => getConnectionIndex(pool.clients.size, serialIndex)
      }
    } yield fallback(pool.clients(j)))

  }

  private[this] def getConnectionIndex(size: Int, num: Int): Int = {
    size match {
      //size > 1
      case x if x == (num + 1) && num > 0 => num - 1
      //size == 1
      case x if x == (num + 1) && num == 0 => -1
      case _ => num + 1
    }
  }

}