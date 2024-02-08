package org.hyperdiary.solid.client

import sttp.client3.*
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.{ Header, MediaType, Uri }

class SolidClient {

  private val backend = OkHttpSyncBackend()

  /** Creates a resource for a given URI using PUT
    *
    * @param resourceUri
    *   \- the URI of the resource
    * @param bodyText
    *   \- the request body text
    * @param mediaType
    *   \- the request media type
    * @return
    *   the wrapped response
    */
  def putResource(
    resourceUri: Uri,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] =
    basicRequest.body(bodyText).contentType(mediaType.toString).put(resourceUri).send(backend)

  /** Creates a Linked Data Platform Container using PUT
    * @param containerUri
    *   \- the URI of the container
    * @return
    *   the wrapped response
    */
  def putContainer(
    containerUri: Uri
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .put(containerUri)
      .headers(Header("Link", "<http://www.w3.org/ns/ldp#Container>; rel=\"type\""))
      .send(backend)

  /** Creates a resource with a generated URI using POST
    *
    * @param baseUri
    *   \- the base URI of the resource to be generated
    * @param bodyText
    *   \- the request body text
    * @param mediaType
    *   \- the request media type
    * @return
    *   the wrapped response
    */
  def postResource(
    baseUri: Uri,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .body(bodyText)
      .contentType(mediaType)
      .post(baseUri)
      .send(backend)

  /** Deletes the given resource URI using DELETE
    *
    * @param resourceUri
    *   \- the resource URI
    * @return
    *   the wrapped response
    */
  def deleteResource(
    resourceUri: Uri
  ): Identity[Response[Either[String, String]]] =
    basicRequest.delete(resourceUri).send(backend)

  /** Retrieves the given resource URI using GET
    *
    * @param resourceUri
    *   \- the resource URI
    * @param acceptType
    *   \- the accepted response content type
    * @return
    *   the wrapped response
    */
  def getResource(
    resourceUri: Uri,
    acceptType: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .get(resourceUri)
      .header(Header.accept(acceptType))
      .send(backend)

  /** Retrieves the headers for the given resource URI using HEAD
    *
    * @param resourceUri
    *   \- the resource URI
    * @param acceptType
    *   \- the accepted response content type
    * @return
    *   the wrapped response
    */
  def getResourceHeaders(
    resourceUri: Uri,
    acceptType: String
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .head(resourceUri)
      .header(Header.accept(acceptType))
      .send(backend)

  /** Retrieves the headers for the given container URI using HEAD
    * @param containerUri
    *   \- the container URI
    * @return
    *   the wrapped response
    */
  def getContainerHeaders(containerUri: Uri): Identity[Response[Either[String, String]]] =
    basicRequest.head(containerUri).send(backend)

  /** Retrieves the communication options for the given URI using OPTIONS
    *
    * @param resourceUri
    *   \- the resource URI
    * @return
    *   the wrapped response
    */
  def getResourceOptions(
    resourceUri: Uri
  ): Identity[Response[Either[String, String]]] =
    basicRequest.options(resourceUri).send(backend)

  /** Modifies the given resource URI using PATCH
    *
    * @param resourceUri
    *   \- the resource URI
    * @param bodyText
    *   \- the patch command body in N3 Patch or SPARQL Update format
    * @param mediaType
    *   \- the patch update content type (e.g. text/n3 or application/sparql-update)
    * @return
    *   the wrapped response
    */
  def patchResource(
    resourceUri: Uri,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]] =
    basicRequest
      .patch(resourceUri)
      .body(bodyText)
      .contentType(mediaType.toString)
      .send(backend)

}
