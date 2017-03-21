package com.maogogo.teaching.rest.filters

import com.twitter.finagle._
import com.twitter.finagle.http.{ Status => HttpStatus, _ }
import com.twitter.util.Future
import com.twitter.io.Buf
import org.slf4j.LoggerFactory
import com.maogogo.teaching.Wrapped
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import org.json4s.ext.JodaTimeSerializers
import com.maogogo.teaching.thrift._

class ExceptionsFilter extends SimpleFilter[Request, Response] {

  lazy val log = LoggerFactory.getLogger(getClass)
  implicit val formats = Serialization.formats(NoTypeHints)

  def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request) handle {
      case e @ CommonException(ex) =>
        log.error("common exception", e)
        respond(request, HttpStatus.Ok, Some(write(Wrapped("", 501, Some(ex._1)))))
      case e =>
        log.error("exception", e)
        respond(request, HttpStatus.InternalServerError, Some(write(Wrapped("", 500, Some(e.getMessage)))))
    }
  }

  private def respond(request: Request, responseStatus: HttpStatus, jsonStr: Option[String]): Response = {
    val resp = Response(responseStatus)
    resp.contentType = "application/json"
    resp.content = Buf.Utf8(jsonStr.getOrElse("{\"code\":\"INTERNAL_SERVER_ERROR\"}"))
    resp
  }

}