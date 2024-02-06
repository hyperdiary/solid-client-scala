package org.hyperdiary

import sttp.model.{MediaType, StatusCode}

class SolidClientSuite extends munit.FunSuite {

  private val hostname = "http://localhost:3000/"
  private val client = SolidClient(hostname)
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

  test("create, retrieve and delete a named plain text resource") {
    val resourceName = "myfile.txt"
    val response1 = client.createResource(resourceName,textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)
    val response2 = client.getResource(resourceName,"text/plain")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(textBody))
    val response3 = client.deleteResource(resourceName)
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("create, retrieve and delete a named turtle resource") {
    val resourceName = "myfile.ttl"
    val response1 = client.createResource(resourceName, turtleBody, MediaType("text","turtle"))
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.getResource(resourceName, "text/turtle")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(turtleBody))

    val response3 = client.getResource(resourceName, "application/ld+json")
    assertEquals(response3.code, StatusCode.Ok)
    assertEquals(response3.body, Right(jsonLdBody))

    val response4 = client.deleteResource(resourceName)
    assertEquals(response4.code, StatusCode.ResetContent)
  }

  test("create, retrieve and delete a generated plain text resource") {
    val response1 = client.createResource(textBody, MediaType.TextPlain)
    assertEquals(response1.code, StatusCode.Created)
    val generatedResourceName = response1.headers.find(_.name == "Location").get.value.substring(hostname.length)
    val response2 = client.getResource(generatedResourceName,"text/plain")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(textBody))
    val response3 = client.deleteResource(generatedResourceName)
    assertEquals(response3.code, StatusCode.ResetContent)
  }

  test("create, retrieve and delete a generated turtle resource") {
    val response1 = client.createResource(turtleBody, MediaType("text","turtle"))
    assertEquals(response1.code, StatusCode.Created)
    val generatedResourceName = response1.headers.find(_.name == "Location").get.value.substring(hostname.length)
    val response2 = client.getResource(generatedResourceName,"text/turtle")
    assertEquals(response2.code, StatusCode.Ok)
    assertEquals(response2.body, Right(turtleBody))
    val response3 = client.deleteResource(generatedResourceName)
    assertEquals(response3.code, StatusCode.ResetContent)
  }

}
