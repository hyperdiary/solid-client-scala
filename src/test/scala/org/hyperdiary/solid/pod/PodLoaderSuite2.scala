package org.hyperdiary.solid.pod

import org.hyperdiary.solid.client.SolidClient
import org.hyperdiary.solid.dpop.DpopManager

class PodLoaderSuite2 extends munit.FunSuite {

  // FIXME - this should be a unit test and the PodLoaderSuite should be an integration test

  private val rootPath = "/home/rkw"; // "/Users/devexe"
  private val journalRdfRoot = s"$rootPath/Source/GitHub/hyperdiary/journal-rdf"

//  test("test6") {
//    val client = new SolidClient(DpopManager())
//    val hostname = "http://waltons.example.org"
//    val loader = new PodLoader(client, hostname)
//    loader.loadTurtleFile(s"$journalRdfRoot/turtle/waltons/Waltons.ttl")
//  }

}
