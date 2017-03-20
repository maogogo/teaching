package com.maogogo.teaching.rest.filters

import com.twitter.finagle._
import com.twitter.finagle.http.{ Status => HttpStatus, _ }
import com.twitter.util.Future
import com.twitter.io.Buf
import org.slf4j.LoggerFactory

class ExceptionsFilter extends SimpleFilter[Request, Response] {

  lazy val log = LoggerFactory.getLogger(getClass)

  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request) handle {
      case e =>
        log.error("exception", e)
        respond(request, HttpStatus.InternalServerError, Some("{\"error\":500}"))
    }
  }

  private def respond(request: Request, responseStatus: HttpStatus, jsonStr: Option[String]): Response = {
    val resp = Response(responseStatus)
    resp.contentType = "application/json"
    resp.content = Buf.Utf8(jsonStr.getOrElse("{\"code\":\"INTERNAL_SERVER_ERROR\"}"))
    resp
  }

}