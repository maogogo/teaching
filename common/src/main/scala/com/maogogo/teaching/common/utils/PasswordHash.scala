package com.maogogo.teaching.common.utils

object PasswordHash {

  def encodePassword(password: String): (String, String) = {
    val salt = UUID.simpleId
    (encodePassword(password, salt), salt)
  }

  def encodePassword(password: String, salt: String): String =
    DigistUtil.md5(password + salt)

  def checkPasswordBySalt(password: String, passwordHash: String, salt: Option[String] = None): Boolean =
    encodePassword(password, salt.getOrElse("")) == passwordHash

}