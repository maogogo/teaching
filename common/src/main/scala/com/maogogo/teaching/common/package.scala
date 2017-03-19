package com.maogogo.teaching

import com.twitter.finagle.mysql._

package object common {

  type Bytes = Array[Byte]
  type TransactionsClient = Client with Transactions

}