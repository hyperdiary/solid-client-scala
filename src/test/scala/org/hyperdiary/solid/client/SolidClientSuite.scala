package org.hyperdiary.solid.client

import sttp.client3.UriContext
import sttp.model.{MediaType, StatusCode}

class SolidClientSuite extends munit.FunSuite {

  private val hostname = "http://localhost:3000"
  private val client = new SolidClient()
  private val textBody = "abc"
  private val turtleBody = "<ex:s> <ex:p> <ex:o>."
  private val jsonLdBody =
    """[
      |  {
      |    "@id": "ex:s",
      |    "ex:p": [
      |      {
      |        "@id": "ex:o"
      |      }
      |    ]
      |  }
      |]
      |""".stripMargin

  private val patchedTurtleBody =
    """<ex:s> <ex:p> <ex:o>.
      |<ex:s2> <ex:p2> <ex:o2>.
      |""".stripMargin

  test("#1 create, retrieve and delete a named plain text resource") {
    val resourceName = "text1.txt"
    val response1 =
      client.putResource(uri"$hostname/$resourceName", textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.getResource(uri"$hostname/$resourceName", "text/plain")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(textBody))

    val response3 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("#2 create, retrieve headers and delete a named plain text resource") {
    val resourceName = "test2.txt"
    val response1 =
      client.putResource(uri"$hostname/$resourceName", textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.getResourceHeaders(uri"$hostname/$resourceName", "text/plain")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(
      response2.headers.find(_.name == "Content-Type").get.value,
      "text/plain; charset=utf-8"
    )

    val response3 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("#3 create, retrieve options and delete a named plain text resource") {
    val resourceName = "test3.txt"
    val response1 =
      client.putResource(uri"$hostname/$resourceName", textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.getResourceOptions(uri"$hostname/$resourceName")
    assertEquals(response2.code, StatusCode.NoContent)
    assertEquals(
      response2.headers
        .find(_.name == "Access-Control-Allow-Methods")
        .get
        .value,
      "GET,HEAD,OPTIONS,POST,PUT,PATCH,DELETE"
    )

    val response3 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("#4 create, retrieve and delete a named turtle resource") {
    val resourceName = "test4.ttl"
    val response1 = client.putResource(
      uri"$hostname/$resourceName",
      turtleBody,
      MediaType("text", "turtle")
    )
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.getResource(uri"$hostname/$resourceName", "text/turtle")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(turtleBody))

    val response3 = client.getResource(uri"$hostname/$resourceName", "application/ld+json")
    assertEquals(response3.code, StatusCode.Ok)
    assertEquals(response3.body, Right(jsonLdBody))

    val response4 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response4.code, StatusCode.ResetContent)
  }

  test("#5 create, retrieve and delete a generated plain text resource") {
    val response1 = client.postResource(uri"$hostname/",textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)

    val generatedResourceName = response1.headers
      .find(_.name == "Location")
      .get
      .value
      .substring(hostname.length + 1)
    val response2 = client.getResource(uri"$hostname/$generatedResourceName", "text/plain")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(textBody))

    val response3 = client.deleteResource(uri"$hostname/$generatedResourceName")
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("#6 create, retrieve and delete a generated turtle resource") {
    val response1 =
      client.postResource(uri"$hostname/", turtleBody, MediaType("text", "turtle"))
    assertEquals(response1.code, StatusCode.Created)

    val generatedResourceName = response1.headers
      .find(_.name == "Location")
      .get
      .value
      .substring(hostname.length + 1)
    val response2 = client.getResource(uri"$hostname/$generatedResourceName", "text/turtle")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(turtleBody))

    val response3 = client.deleteResource(uri"$hostname/$generatedResourceName")
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("#7 create, patch and delete a resource using N3 Patch") {
    val resourceName = "test7.ttl"
    val response1 = client.putResource(
      uri"$hostname/$resourceName",
      turtleBody,
      MediaType("text", "turtle")
    )
    assertEquals(response1.code, StatusCode.Created)

    val patchBody =
      "@prefix solid: <http://www.w3.org/ns/solid/terms#>. _:rename a solid:InsertDeletePatch; solid:inserts { <ex:s2> <ex:p2> <ex:o2>. }."
    val response2 =
      client.patchResource(uri"$hostname/$resourceName", patchBody, MediaType("text", "n3"))
    assertEquals(response2.code, StatusCode.ResetContent)

    val response3 = client.getResource(uri"$hostname/$resourceName", "text/turtle")
    assertEquals(response3.code, StatusCode.Ok)
    assertEquals(response3.body, Right(patchedTurtleBody))

    val response4 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response4.code, StatusCode.ResetContent)
  }

  test("#8 create, patch and delete a resource using SPARQL Update") {
    val resourceName = "test8.ttl"
    val response1 = client.putResource(
      uri"$hostname/$resourceName",
      turtleBody,
      MediaType("text", "turtle")
    )
    assertEquals(response1.code, StatusCode.Created)

    val patchBody = "INSERT DATA { <ex:s2> <ex:p2> <ex:o2> }"
    val response2 = client.patchResource(
      uri"$hostname/$resourceName",
      patchBody,
      MediaType("application", "sparql-update")
    )
    assertEquals(response2.code, StatusCode.ResetContent)

    val response3 = client.getResource(uri"$hostname/$resourceName", "text/turtle")
    assertEquals(response3.code, StatusCode.Ok)
    assertEquals(response3.body, Right(patchedTurtleBody))

    val response4 = client.deleteResource(uri"$hostname/$resourceName")
    assertEquals(response4.code, StatusCode.ResetContent)
  }

}
