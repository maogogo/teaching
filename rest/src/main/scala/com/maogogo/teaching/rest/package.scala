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
import com.maogogo.teaching.thrift._
import java.util.Date

package object rest {

  private val log = LoggerFactory.getLogger(getClass)

  implicit val formats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  //Decode.instance[A, Application.Json]((buf, cs) => Try(parse(BufText.extract(buf, cs), false).extract[A]))
  implicit def decode[A: Manifest]: Decode.Json[A] =
    Decode.instance[A, Application.Json]((buf, cs) => Try(parse(buf.asString(cs), false).extract[A]))

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
        log.error(s"exception#${e.description}", e)
        wrappedError(400, s"exception#${e.description}")
      case e: InvalidGrant =>
        //缺少请求参数
        log.error(s"invalid grant exception#${e.description}", e)
        wrappedError(401, s"exception#${e.description}")
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

  implicit def toAccessToken(at: TAccessToken) = AccessToken(at.token, at.refreshToken, at.scope, at.expiresIn, new Date(at.createdAt))

  implicit def toOptionAccessToken(at: Option[TAccessToken]) = at.map(toAccessToken)

  implicit def toTAccessToken(at: AccessToken) = TAccessToken(at.token, at.refreshToken, at.scope, at.expiresIn, at.createdAt.getTime)

  implicit def toAuthInfo(ai: TAuthInfo) = AuthInfo[TSession](user = ai.session, ai.clientId, ai.scope, ai.redirectUri)

  implicit def toOptionAuth(ai: Option[TAuthInfo]) = ai.map(toAuthInfo)

  implicit def toTAuthInfo(ai: AuthInfo[TSession]) = TAuthInfo(ai.user, ai.clientId, ai.scope, ai.redirectUri)

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