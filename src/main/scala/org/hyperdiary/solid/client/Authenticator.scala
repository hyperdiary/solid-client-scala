package org.hyperdiary.solid.client

import io.circe.generic.auto.*
import io.circe.syntax.*
import org.hyperdiary.solid.dpop.DpopManager
import org.hyperdiary.solid.model.{AccessToken, Credentials, Token, WebId}
import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.{Header, MediaType, Uri}

import java.net.{URI, URLEncoder}
import java.nio.charset.StandardCharsets
import java.util.Base64

class Authenticator(dpopManager: DpopManager) {

  private val backend = OkHttpSyncBackend()

  /** Extract the login URL from the account API
    * @param controlsUri
    * @return
    */
  def getLoginUrl(controlsUri: Uri): Option[String] =
    basicRequest.get(controlsUri).header(Header.accept("application/json")).send(backend).body match {
      case Left(_)         => None
      case Right(controls) => Some(JsonHelper.extractLoginUrl(JsonHelper.parseJson(controls)))
    }

  /** Login to the account using email and password and receive an authorization token
    * @param loginUrl
    * @param credentials
    * @return
    */
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

  /** Using the authorization token, extract the client credentials URL from the account API
    * @param controlsUri
    * @param token
    * @return
    */
  def getClientCredentialsUrl(controlsUri: Uri, token: String): Option[String] =
    basicRequest
      .get(controlsUri)
      .headers(Header.accept("application/json"), Header.authorization("CSS-Account-Token", token))
      .send(backend)
      .body match {
      case Left(_)         => None
      case Right(controls) => Some(JsonHelper.extractClientCredentialsUrl(JsonHelper.parseJson(controls)))
    }

  /** Using the authorization token, retrieve an access token from the client credentials URL
    * @param clientCredentialsUrl
    * @param token
    * @param webId
    * @return
    */
  def generateToken(clientCredentialsUrl: Uri, token: String, webId: WebId): Option[Token] =
    basicRequest
      .body(webId.asJson.toString)
      .contentType(MediaType.ApplicationJson)
      .header(Header.authorization("CSS-Account-Token", token))
      .post(clientCredentialsUrl)
      .send(backend)
      .body match {
      case Left(_)        => None
      case Right(content) => Some(JsonHelper.extractToken(JsonHelper.parseJson(content)))
    }

  def generateAccessToken(tokenUrl: Uri, token: Token): Option[AccessToken] = {
    val urlEncodedId = URLEncoder.encode(token.id, "UTF-8")
    val urlEncodedSecret = URLEncoder.encode(token.secret, "UTF-8")
    val authString = s"$urlEncodedId:$urlEncodedSecret"
    val base64AuthString = Base64.getEncoder.encodeToString(authString.getBytes(StandardCharsets.UTF_8))
    val dpopHeader = dpopManager.generateProof("ES256", tokenUrl.toJavaUri,"POST").toOption.get
    basicRequest
      .body("grant_type=client_credentials&scope=webid")
      .contentType(MediaType.ApplicationXWwwFormUrlencoded)
      .auth
      .basicToken(base64AuthString)
      .header("dpop", dpopHeader)
      .post(tokenUrl)
      .send(backend)
      .body match {
      case Left(_)        => None
      case Right(accessToken) => Some(JsonHelper.extractAccessToken(JsonHelper.parseJson(accessToken)))
      // end of authorization flow
    }
  }
  
  def authenticatedRequest(requestUrl: Uri, accessToken:String) = {
    basicRequest.auth.bearer(accessToken)
  }

  def getControls(controlsUri: Uri, acceptType: String): Identity[Response[Either[String, String]]] =
    basicRequest.get(controlsUri).header(Header.accept("application/json")).send(backend)

  def login(loginUrl: Uri, credentials: Credentials): Identity[Response[Either[String, String]]] =
    basicRequest
      .body(credentials.asJson.toString)
      .contentType(MediaType.ApplicationJson)
      .post(loginUrl)
      .send(backend)

}
