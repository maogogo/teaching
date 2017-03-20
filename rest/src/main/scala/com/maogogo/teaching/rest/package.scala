package com.maogogo.teaching

import org.slf4j.LoggerFactory
import com.twitter.util._
import io.finch._
import io.finch.internal._
import com.twitter.finagle.http._
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.jackson.Serialization._
import org.json4s.jackson.JsonMethods._
import com.twitter.io._
import shapeless._
import com.twitter.finagle.oauth2._

package object rest {

  private val log = LoggerFactory.getLogger(getClass)

  implicit val formats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  implicit def decode[A: Manifest]: Decode.Json[A] =
    Decode.instance[A, Application.Json]((buf, cs) => Try(parse(BufText.extract(buf, cs), false).extract[A]))

  implicit def genCoprodUpdateRepr[T, R <: HList, Repr <: Coproduct](implicit
    coprod: HasCoproductGeneric[T],
    gen: Generic.Aux[T, Repr],
    update: Lazy[UpdateRepr[Repr, R]]): UpdateRepr[T, R] =
    new UpdateRepr[T, R] {
      def apply(t: T, r: R): T = gen.from(update.value(gen.to(t), r))
    }

  implicit def encodeJson[A <: AnyRef]: ToResponse.Aux[A, Application.Json] =
    ToResponse.instance[A, Application.Json] { (a, cs) =>
      val resp = Response(Status.Ok)
      resp.contentType = "application/json"
      resp.content = Buf.Utf8(a)
      resp
    }

  private[this] implicit def writeJson[A <: AnyRef](a: A): String = {
    a match {
      case Error.NotPresent(e) =>
        log.error(s"exception#${e.description}#${e}", e)
        wrappedError(400, s"exception#${e.description}")
      case Error.NotParsed(e, _, c) =>
        log.error("exception", c)
        wrappedError(400, s"exception#${e.description}#${c.getMessage}")
      case Error.NotValid(e, _) =>
        wrappedError(400, s"exception#${e.description}")
      case e: InvalidRequest =>
        log.error(s"exception#${e.errorType}")
        wrappedError(401, s"exception#${e.errorType}")
      case e: InvalidGrant =>
        log.error(s"invalid grant exception#${e.errorType}")
        wrappedError(401, s"exception#${e.errorType}")
      case e: Throwable =>
        log.error("exception", e)
        wrappedError(error = e.getMessage)
      case x =>
        val cleaned = Extraction.decompose(x).removeField {
          case JField("_passthroughFields", _) => true
          case _ => false
        }
        write(Wrapped(removeHeadAndTail(cleaned)))
    }
  }

  private[this] def wrappedError(status: Int = 500, error: String): String = {
    write(Wrapped("", status, Some(error)))
  }

  private[this] def removeHeadAndTail(input: JValue): JValue = {
    input match {
      case JObject(List(("tail" | "head", rest))) => removeHeadAndTail(rest)
      case x => x
    }
  }
}

case class Wrapped[T](data: T, status: Int = 200, error: Option[String] = None)

trait UpdateRepr[T, R <: HList] {
  def apply(t: T, r: R): T
}