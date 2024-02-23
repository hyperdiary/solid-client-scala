package org.hyperdiary.solid.client

import io.circe.generic.auto.*
import io.circe.syntax.*
import org.hyperdiary.solid.model.Credentials
import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.{ Header, MediaType, Uri }

class Authenticator {

  private val backend = OkHttpSyncBackend()

  def getLoginUrl(controlsUri: Uri): Option[String] =
    basicRequest.get(controlsUri).header(Header.accept("application/json")).send(backend).body match {
      case Left(_)         => None
      case Right(controls) => Some(JsonHelper.extractLoginUrl(JsonHelper.parseJson(controls)))
    }

  def getAuthorization(loginUrl: Uri, credentials: Credentials): Option[String] =
    basicRequest
      .body(credentials.asJson.toString)
      .contentType(MediaType.ApplicationJson)
      .post(loginUrl)
      .send(backend)
      .body match {
      case Left(_)              => None
      case Right(authorization) => Some(JsonHelper.extractAuthorization(JsonHelper.parseJson(authorization)))
    }

    def getClientCredentialsUrl() = ??? // TODO

  def getControls(controlsUri: Uri, acceptType: String): Identity[Response[Either[String, String]]] =
    basicRequest.get(controlsUri).header(Header.accept("application/json")).send(backend)

  def login(loginUrl: Uri, credentials: Credentials): Identity[Response[Either[String, String]]] =
    basicRequest
      .body(credentials.asJson.toString)
      .contentType(MediaType.ApplicationJson)
      .post(loginUrl)
      .send(backend)

}
