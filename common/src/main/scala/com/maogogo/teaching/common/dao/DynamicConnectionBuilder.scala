package com.maogogo.teaching.common.dao

import com.maogogo.teaching.common._
import javax.inject.Inject
import com.twitter.finagle.mysql._
import javax.inject.Named
import org.slf4j.LoggerFactory
import com.twitter.util._
import com.maogogo.teaching.common.modules.MySqlPool

class DynamicConnectionBuilder(connections: Map[String, Seq[MySqlPool]]) extends ConnectionBuilder {

  /**
   * 数据源名称
   */
  def build[T](name: Option[String] = None, partition: Int = 0, serial: Int = 0)(fallback: TransactionsClient => Future[T]): Future[T] = {
    val _connections = name match {
      case Some(s) if !s.isEmpty => connections.get(s).getOrElse(Seq.empty)
      case _ => connections.headOption.map(_._2).getOrElse(Seq.empty)
    }
    buildConnection(_connections, partition, serial)(fallback)
  }

}