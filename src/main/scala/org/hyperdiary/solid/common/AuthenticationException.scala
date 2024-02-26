package org.hyperdiary.solid.common

case class AuthenticationException(message: String) extends ClientException(message)