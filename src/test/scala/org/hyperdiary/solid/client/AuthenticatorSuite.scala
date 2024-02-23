package org.hyperdiary.solid.client

import sttp.client3.UriContext
import sttp.model.{MediaType, StatusCode, Uri}
import io.circe.*
import io.circe.parser.*
import org.hyperdiary.solid.model.{Credentials, WebId}

class AuthenticatorSuite extends munit.FunSuite {

  // DO NOT CHECK VALUES INTO GIT!!!
  private val username = ""
  private val password = ""

  private val authenticator = new Authenticator()

  private val hostname = "http://localhost:3000"
  private val controlsUri = uri"$hostname/.account/"

  test("#1 get the login URL") {
    val loginUrl = authenticator.getLoginUrl(controlsUri)
    assertEquals(loginUrl, Some("http://localhost:3000/.account/login/password/"))
  }

  test("#2 get the login URL and attempt login") {
    authenticator.getLoginUrl(controlsUri) match {
      case Some(loginUrl) =>
        val response =
          authenticator.getAuthorization(uri"$loginUrl", Credentials(username, password))
        assert(response.nonEmpty)
      case None => fail("No login URL received")
    }
  }

  test("#3 get the login URL, attempt login and then get the client credentials URL") {
    authenticator.getLoginUrl(controlsUri) match {
      case Some(loginUrl) =>
        val token = authenticator.getAuthorization(uri"$loginUrl", Credentials(username, password))
        val clientCredentialsUrl = authenticator.getClientCredentialsUrl(controlsUri, token.getOrElse("")).getOrElse("")
        authenticator.generateToken(uri"$clientCredentialsUrl",token.get,WebId("my-token","http://krw.localhost:3000/profile/card#me"))
        assertEquals(clientCredentialsUrl, "http://")
      case None => fail("No login URL received")
    }
  }

}
