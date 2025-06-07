package org.hyperdiary.solid.pod

import org.apache.jena.riot.{ Lang, RDFDataMgr }
import org.hyperdiary.solid.client.SolidClient
import org.hyperdiary.solid.dpop.DpopManager
import sttp.client3.UriContext
import sttp.model.Uri

import java.io.{ File, PrintWriter }
import java.nio.file.Path

class PodLoaderSuite extends munit.FunSuite {

  private val rootPath = "/Users/devexe"
  private val journalRdfRoot = s"$rootPath/Source/GitHub/hyperdiary/journal-rdf"
  private val client = new SolidClient(DpopManager())
  private val podUrl = "http://krw.localhost:3000"
  private val baseUrl = "http://krw.hyperdiary.io"
  private val loader = new PodLoader(client, podUrl, Some(baseUrl))

  test("create collections") {
    loader.createContainers(List("label", "person", "place", "residence", "thing", "photo"))
    for {
      resource <- loader.generateResource("label")
    } yield println(resource)
  }

  test("test2".ignore) {
    val collectionName = "label"
    val labelFilePath = getClass.getResource("/labels.csv").getPath
    val labels = loader.readLabelsCsv(labelFilePath: String)
    val rdfLabels: List[(Uri, String)] = labels.flatMap { label =>
      val res = loader.generateResource(collectionName)
      res.flatMap { uri =>
        val id = uri.substring(uri.lastIndexOf('/') + 1)
        Some((uri"$podUrl/$collectionName/$id", label.asRdfString(uri)))
      }
    }
    rdfLabels.foreach(rdfLabel => loader.insertAsSparqlUpdate(rdfLabel._1, rdfLabel._2))
    assertEquals(rdfLabels.length, 7)
  }

  test("test3") {
    val fileRoot = s"$journalRdfRoot/turtle/krw"
    for (i <- 1 to 17) {
      val entriesFilePath = File(s"$fileRoot/J1.E$i.ttl").getPath
      loader.loadTurtleHashFile(entriesFilePath, "entry", s"J1.E$i")
    }
    val journalsFilePath = File(s"$fileRoot/J1.ttl").getPath
    loader.loadTurtleFile(journalsFilePath)
    val labelsFilePath = File(s"$fileRoot/krw-labels.ttl").getPath
    loader.loadTurtleFile(labelsFilePath)
    val peopleFilePath = File(s"$fileRoot/krw-people.ttl").getPath
    loader.loadTurtleFile(peopleFilePath)
    val placesFilePath = File(s"$fileRoot/krw-places.ttl").getPath
    loader.loadTurtleFile(placesFilePath)
    val residencesFilePath = File(s"$fileRoot/krw-residences.ttl").getPath
    loader.loadTurtleFile(residencesFilePath)
    val thingsFilePath = File(s"$fileRoot/krw-things.ttl").getPath
    loader.loadTurtleFile(thingsFilePath)
    val photosFilePath = File(s"$fileRoot/krw-photos.ttl").getPath
    loader.loadTurtleFile(photosFilePath)
  }

  // Used to convert RDF/XML Journal entries to Turtle
  test("test4".ignore) {
    for (i <- 1 to 17) {
      val entriesFilePath = s"/home/rkw/Source/GitHub/hyperdiary/journal-xml/target/J1.E$i.xml"
      val model = RDFDataMgr.loadModel(entriesFilePath)
      val out = new PrintWriter(new File(s"/home/rkw/Source/GitHub/hyperdiary/journal-rdf/turtle/krw/J1.E$i.ttl"))
      RDFDataMgr.write(out, model.listStatements().toModel, Lang.TTL)
      out.close()
      assertEquals(true, true)
    }
    val entriesFilePath = getClass.getResource("/J1.E17.xml").getPath
  }

  test("test5".ignore) {
    for (i <- 3 to 15) {
      val photoFilePath = Path.of(s"$journalRdfRoot/images/krw/$i.jpg")
      loader.loadPhoto(photoFilePath, "photo", s"$i.jpg")
    }
  }

  test("test6".ignore) {
    val waltonsPodUrl = "http://waltons.localhost:3000"
    val waltonsBaseUrl = "http://waltons.example.org"
    val podLoader = new PodLoader(client, waltonsPodUrl, Some(waltonsBaseUrl))
    podLoader.createContainers(List("person"))
  }

  test("test7".ignore) {
    val waltonsPodUrl = "http://waltons.localhost:3000"
    val waltonsBaseUrl = "http://waltons.example.org"
    val podLoader = new PodLoader(client, waltonsPodUrl, Some(waltonsBaseUrl))
    podLoader.loadTurtleFile(s"$journalRdfRoot/turtle/waltons/Waltons.ttl")
  }

  test("test8".ignore) {
    val entriesFilePath = s"/home/rkw/Source/GitHub/hyperdiary/journal-xml/examples/waltons-rdf-entry.xml"
    val model = RDFDataMgr.loadModel(entriesFilePath)
    val out = new PrintWriter(new File(s"/home/rkw/Source/GitHub/hyperdiary/journal-rdf/turtle/waltons/J1.E1.ttl"))
    RDFDataMgr.write(out, model.listStatements().toModel, Lang.TTL)
    out.close()
    assertEquals(true, true)
  }

  test("load entries hash file".ignore) {
    val entriesFilePath = File(s"/home/rkw/Source/GitHub/hyperdiary/journal-rdf/turtle/waltons/waltons-entry.ttl").getPath
    loader.loadTurtleHashFile(entriesFilePath, "entry", "J1.E1")
  }

  test("load xml".ignore) {
    val xmlFilePath = Path.of(s"/Users/devexe/Source/GitHub/rwalpole/journal/2020s/2025/06/02.xml")
    loader.loadXml(xmlFilePath, "entry-xml", "02.xml")
  }

}
