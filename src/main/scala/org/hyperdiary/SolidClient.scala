package org.hyperdiary

import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.{Header, MediaType}

class SolidClient(hostname: String) {

  private val backend = OkHttpSyncBackend()

  def createResource(
      resourceName: String,
      bodyText: String,
      mediaType: MediaType
  ): Identity[Response[Either[String, String]]] = {
    basicRequest
      .body(bodyText)
      .contentType(mediaType.toString)
      .put(uri"$hostname$resourceName")
      .send(backend)
  }

  def createResource(
      bodyText: String,
      mediaType: MediaType
  ): Identity[Response[Either[String, String]]] = {
    basicRequest
      .body(bodyText)
      .contentType(mediaType)
      .post(uri"$hostname")
      .send(backend)
  }

  def deleteResource(
      resourceName: String
  ): Identity[Response[Either[String, String]]] = {
    basicRequest.delete(uri"$hostname$resourceName").send(backend)
  }

  def getResource(
      resourceName: String,
      acceptType: String
  ): Identity[Response[Either[String, String]]] = {
    basicRequest.get(uri"$hostname$resourceName").header(Header.accept(acceptType)).send(backend)

  }

}
