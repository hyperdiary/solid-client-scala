package org.hyperdiary.solid.client

import sttp.client3.UriContext
import sttp.model.{MediaType, StatusCode, Uri}
import io.circe.*
import io.circe.parser.*
import org.hyperdiary.solid.model.Credentials

class AuthenticatorSuite extends munit.FunSuite {

  private val authenticator = new Authenticator()

  private val hostname = "http://localhost:3000"

  test("#1 get the login URL") {
    val loginUrl = authenticator.getLoginUrl(uri"$hostname/.account/")
    assertEquals(loginUrl, Some("http://localhost:3000/.account/login/password/"))
  }

  test("#2 get the login URL and attempt login") {
    authenticator.getLoginUrl(uri"$hostname/.account/") match {
      case Some(loginUrl) =>
        val response = authenticator.getAuthorization(uri"$loginUrl", Credentials("", ""))
        assert(response.nonEmpty)
      case None => fail("No login URL received")
    }
  }

  test("#3 get the login URL, attempt login and then get the client credentials URL") {
    authenticator.getLoginUrl(uri"$hostname/.account/") match {
      case Some(loginUrl) =>
        val response = authenticator.getAuthorization(uri"$loginUrl", Credentials("", ""))
        authenticator.
      case None => fail("No login URL received")
    }
  }

}
