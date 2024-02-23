package org.hyperdiary.solid.client

import io.circe.*
import io.circe.parser.*
import org.hyperdiary.solid.model.Token

object JsonHelper {

  def extractLoginUrl(json: Json): String = {
    val cursor = json.hcursor
    cursor.downField("controls").downField("password").downField("login").as[String].getOrElse("")
  }
  
  def extractAuthorization(json: Json): String = {
    val cursor = json.hcursor
    cursor.downField("authorization").as[String].getOrElse("")
  }
  
  def extractClientCredentialsUrl(json: Json): String = {
    val cursor = json.hcursor
    cursor.downField("controls").downField("account").downField("clientCredentials").as[String].getOrElse("")
  }

  def extractToken(json: Json): Token = {
    val cursor = json.hcursor
//    for {
//      id <- cursor.downField("id").as[String] //.getOrElse("")
//      secret <- cursor.downField("secret").as[String] //.getOrElse("")
//      resource <- cursor.downField("resource").as[String] //.getOrElse("")
//    } yield Token(id, secret, resource)
      val id = cursor.downField("id").as[String].getOrElse("")
      val secret = cursor.downField("secret").as[String].getOrElse("")
      val resource = cursor.downField("resource").as[String].getOrElse("")
    Token(id, secret, resource)
  }

  def parseJson(jsonString: String): Json = parse(jsonString).getOrElse(Json.Null)

}
