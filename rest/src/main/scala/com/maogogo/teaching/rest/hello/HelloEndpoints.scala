package com.maogogo.teaching.rest.hello

import io.finch._

class HelloEndpoints {

  def endpoints = hello

  val hello: Endpoint[String] = get("hello") {
    //service.hello("Toan").map(Ok)

    Ok("Hello")
  }

}