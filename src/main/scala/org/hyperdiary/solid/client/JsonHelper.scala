package org.hyperdiary.solid.client

import io.circe._, io.circe.parser._

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

  def parseJson(jsonString: String): Json = parse(jsonString).getOrElse(Json.Null)

}
