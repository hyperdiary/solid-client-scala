package org.hyperdiary

import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.{ Header, MediaType }

class SolidClient(hostname: String) {

  private val backend = OkHttpSyncBackend()

  /**
   * Creates a resource for a given URL using PUT
   *
   * @param resourceName - the local name of the resource
   * @param bodyText - the request body text
   * @param mediaType - the request media type
   * @return the wrapped response
   */
  def createResource(
    resourceName: String,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .body(bodyText)
      .contentType(mediaType.toString)
      .put(uri"$hostname$resourceName")
      .send(backend)

  /**
   * Creates a resource with a generated URL using POST
   *
   * @param bodyText - the request body text
   * @param mediaType - the request media type
   * @return the wrapped response
   */
  def createResource(
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .body(bodyText)
      .contentType(mediaType)
      .post(uri"$hostname")
      .send(backend)

  /**
   * Deletes the given resource URL using DELETE
   *
   * @param resourceName - the resource local name
   * @return the wrapped response
   */
  def deleteResource(
    resourceName: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest.delete(uri"$hostname$resourceName").send(backend)

  /**
   * Retrieves the given resource URL using GET
   *
   * @param resourceName - the resource local name
   * @param acceptType - the accepted response content type
   * @return the wrapped response
   */
  def getResource(
    resourceName: String,
    acceptType: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .get(uri"$hostname$resourceName")
      .header(Header.accept(acceptType))
      .send(backend)

  /**
   * Retrieves the resource headers for the given URL using HEAD
   *
   * @param resourceName - the resource local name
   * @param acceptType - the accepted response content type
   * @return the wrapped response
   */
  def getResourceHeaders(
    resourceName: String,
    acceptType: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .head(uri"$hostname$resourceName")
      .header(Header.accept(acceptType))
      .send(backend)

  /**
   * Retrieves the communication options for the given URL using OPTIONS
   *
   * @param resourceName - the resource local name
   * @return the wrapped response
   */
  def getResourceOptions(
    resourceName: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest.options(uri"$hostname$resourceName").send(backend)

  /**
   * Modifies the given resource URL using PATCH
   *
   * @param resourceName - the resource local name
   * @param bodyText - the patch command body in N3 Patch or SPARQL Update format
   * @param mediaType - the patch update content type (e.g. text/n3 or application/sparql-update)
   * @return the wrapped response
   */
  def patchResource(
    resourceName: String,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] = {
    val request = basicRequest
      .patch(uri"$hostname$resourceName")
      .body(bodyText)
      .contentType(mediaType.toString)
    println(request.toCurl)
    request.send(backend)
  }

}
