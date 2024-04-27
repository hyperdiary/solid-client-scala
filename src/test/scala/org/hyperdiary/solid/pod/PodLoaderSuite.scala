package org.hyperdiary.solid.pod

import org.apache.jena.riot.{Lang, RDFDataMgr}
import sttp.client3.UriContext
import sttp.model.Uri

import java.io.{File, PrintWriter}
import java.nio.file.Path

class PodLoaderSuite extends munit.FunSuite {

  private val rootPath = "/Users/devexe"; // "/home/rkw"
  private val journalRdfRoot = s"$rootPath/Source/GitHub/hyperdiary/journal-rdf"

  test("test".ignore) {
    val loader = new PodLoader("http://krw.localhost:3000/")
    loader.createContainers(List("label","person","place","residence","thing","photo"))
    for {
      resource <- loader.generateResource("label")
    } yield println(resource)
  }

  test("test2".ignore) {
    val hostname = "http://krw.localhost:3000/"
    val collectionName = "label"
    val loader = new PodLoader(hostname)
    val labelFilePath = getClass.getResource("/labels.csv").getPath
    val labels = loader.readLabelsCsv(labelFilePath: String)
    val rdfLabels: List[(Uri,String)] = labels.flatMap { label =>
      val res = loader.generateResource(collectionName)
      res.flatMap(uri =>
        val id = uri.substring(uri.lastIndexOf('/') + 1)
        Some((uri"$hostname$collectionName/$id", label.asRdfString(uri))))
    }
    rdfLabels.foreach(rdfLabel => loader.insertAsSparqlUpdate(rdfLabel._1,rdfLabel._2))
    assertEquals(rdfLabels.length, 7)
  }

  test("test3".ignore) {
    val hostname = "http://krw.localhost:3000/"
    val fileRoot = s"$journalRdfRoot/turtle/krw"
    val loader = new PodLoader(hostname)
    for(i <- 1 to 17) {
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

  test("test4".ignore) {
    for(i <- 1 to 17) {
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
    val hostname = "http://krw.localhost:3000/"
    val loader = new PodLoader(hostname)
    for(i <- 3 to 15) {
      val photoFilePath = Path.of(s"$journalRdfRoot/images/krw/$i.jpg")
      loader.loadPhoto(photoFilePath, "photo", s"$i.jpg")
    }
  }

}
