package com.maogogo.teaching

import com.twitter.util.Future

package object rpc {

  implicit def optionFuture2SeqFuture[T](f: Future[Option[T]]): Future[Seq[T]] = f map {
    case Some(x) => Seq(x)
    case None => Seq.empty[T]
  }

}