package org.hyperdiary.solid.client

import sttp.client3.{ Identity, Response }
import sttp.model.{ MediaType, Uri }

import java.nio.file.Path

trait Client {

  def putResource(
    resourceUri: Uri,
    bodyText: String,
    mediaType: MediaType
  ): Identity[Response[Either[String, String]]]

  def putResource(resourceUri: Uri, filePath: Path, mediaType: MediaType): Identity[Response[Either[String, String]]]

  /** Creates a Linked Data Platform Container using PUT
    *
    * @param containerUri
    *   \- the URI of the container
    * @return
    *   the wrapped response
    */
  def putContainer(
    containerUri: Uri
  ): Identity[Response[Either[String, String]]]

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
  ): Identity[Response[Either[String, String]]]

  /** Deletes the given resource URI using DELETE
    *
    * @param resourceUri
    *   \- the resource URI
    * @return
    *   the wrapped response
    */
  def deleteResource(
    resourceUri: Uri
  ): Identity[Response[Either[String, String]]]

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
  ): Identity[Response[Either[String, String]]]

  def getResourceWithAuthorization(
    resourceUri: Uri,
    acceptType: String,
    authToken: String
  ): Identity[Response[Either[String, String]]]

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
  ): Identity[Response[Either[String, String]]]

  /** Retrieves the headers for the given container URI using HEAD
    *
    * @param containerUri
    *   \- the container URI
    * @return
    *   the wrapped response
    */
  def getContainerHeaders(containerUri: Uri): Identity[Response[Either[String, String]]]

  /** Retrieves the communication options for the given URI using OPTIONS
    *
    * @param resourceUri
    *   \- the resource URI
    * @return
    *   the wrapped response
    */
  def getResourceOptions(
    resourceUri: Uri
  ): Identity[Response[Either[String, String]]]

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
  ): Identity[Response[Either[String, String]]]

}
