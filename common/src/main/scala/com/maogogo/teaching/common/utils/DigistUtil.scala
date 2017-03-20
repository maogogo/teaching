package com.maogogo.teaching.common.utils

import java.security.MessageDigest
import java.util.Base64

object DigistUtil {

  def md5(str: String = java.util.UUID.randomUUID().toString) = digest("MD5", str)
  def md5U(str: String = java.util.UUID.randomUUID().toString) = digest("MD5", str).toUpperCase
  def sha1(str: String = java.util.UUID.randomUUID().toString) = digest("SHA-1", str)
  def sha256(str: String = java.util.UUID.randomUUID().toString) = digest("SHA-256", str)
  def authBasic(clientId: String, clientSecret: String = java.util.UUID.randomUUID().toString) =
    new String(Base64.getMimeEncoder.encode(s"${clientId}:${clientSecret}".getBytes))

  private[this] def digest(name: String, str: String) = {
    if (str == null) null
    else {
      val messageDigest = MessageDigest.getInstance(name)
      messageDigest.update(str.getBytes)
      bytes2Hex(messageDigest.digest)
    }
  }

  private[this] def bytes2Hex(bts: Array[Byte]): String = {
    bts.foldLeft("") { (s, b) =>
      Integer.toHexString(b & 0xFF) match {
        case x if x.length() == 1 => s"${s}0${x}"
        case x => s"${s}${x}"
      }
    }
  }

}