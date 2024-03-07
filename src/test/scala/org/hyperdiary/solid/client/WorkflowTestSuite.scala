package org.hyperdiary.solid.client

import org.hyperdiary.solid.dpop.DpopManager
import sttp.client3.UriContext
import sttp.model.{MediaType, StatusCode}

class WorkflowTestSuite extends munit.FunSuite {

  private val hostname = "http://localhost:3000"
  private val client = new SolidClient(DpopManager())

  /** This test runs a workflow to edit a description resource. Based on
    * https://communitysolidserver.github.io/CommunitySolidServer/latest/usage/metadata/#example-of-a-workflow-for-editing-a-description-resource
    */

  test("workflow to edit a description resource") {

    val response1 = client.putContainer(uri"$hostname/inbox/")
    assertEquals(response1.code, StatusCode.Created)

    val response2 = client.putContainer(uri"$hostname/foo/")
    assertEquals(response2.code, StatusCode.Created)

    val response3 = client.getContainerHeaders(uri"$hostname/foo/")
    assertEquals(response3.code, StatusCode.Ok)
    assertEquals(
      response3.headers.filter(_.name == "Link").find(_.value.contains("describedby")).get.value,
      s"<$hostname/foo/.meta>; rel=\"describedby\""
    )

    val patchBody =
      "@prefix solid: <http://www.w3.org/ns/solid/terms#>. <> a solid:InsertDeletePatch; solid:inserts { <http://localhost:3000/foo/> <http://www.w3.org/ns/ldp#inbox> <http://localhost:3000/inbox/>. }."
    val response4 = client.patchResource(uri"$hostname/foo/.meta", patchBody, MediaType("text", "n3"))
    assertEquals(response4.code, StatusCode.ResetContent)

    val response5 = client.getResource(uri"$hostname/foo/.meta", "text/turtle")
    assertEquals(response5.code, StatusCode.Ok)
    assert(
      response5.body
        .asInstanceOf[Right[String, String]]
        .value
        .contains("<http://localhost:3000/foo/> ldp:inbox <http://localhost:3000/inbox/>")
    )

    val response6 = client.deleteResource(uri"$hostname/foo/")
    assertEquals(response6.code, StatusCode.ResetContent)

    val response7 = client.deleteResource(uri"$hostname/inbox/")
    assertEquals(response7.code, StatusCode.ResetContent)

  }

}
