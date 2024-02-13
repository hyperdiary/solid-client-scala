package org.hyperdiary.solid.pod

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import sttp.client3.UriContext
import sttp.model.Uri

import scala.jdk.CollectionConverters.*
import java.io.{File, PrintWriter, StringWriter}

class PodLoaderSuite extends munit.FunSuite {

  test("test") {
    val loader = new PodLoader("http://krw.localhost:3000/")
    //loader.createContainers(List("journal","entry","label","person","place","residence","thing","photo"))
//    for {
//      resource <- loader.generateResource("label")
//    } yield println(resource)
  }

//  test("test2") {
//    val hostname = "http://krw.localhost:3000/"
//    val collectionName = "label"
//    val loader = new PodLoader(hostname)
//    val labelFilePath = getClass.getResource("/labels.csv").getPath
//    val labels = loader.readLabelsCsv(labelFilePath: String)
//    val rdfLabels: List[(Uri,String)] = labels.flatMap { label =>
//      val res = loader.generateResource(collectionName)
//      res.flatMap(uri =>
//        val id = uri.substring(uri.lastIndexOf('/') + 1)
//        Some((uri"$hostname$collectionName/$id", label.asRdfString(uri))))
//    }
//    rdfLabels.foreach(rdfLabel => loader.insertAsSparqlUpdate(rdfLabel._1,rdfLabel._2))
//    assertEquals(rdfLabels.length, 7)
//  }

  test("test3") {
    val hostname = "http://krw.localhost:3000/"
    val loader = new PodLoader(hostname)
    val entriesFilePath = getClass.getResource("/test-entry.ttl").getPath
    loader.loadTurtleHashFile(entriesFilePath,"entry","J1.E1")
//    val entriesFilePath = getClass.getResource("/krw-entries.ttl").getPath
//    loader.loadTurtleFile(entriesFilePath)
//    val journalsFilePath = getClass.getResource("/krw-journals.ttl").getPath
//    loader.loadTurtleFile(journalsFilePath)
//    val labelsFilePath = getClass.getResource("/krw-labels.ttl").getPath
//    loader.loadTurtleFile(labelsFilePath)
//    val peopleFilePath = getClass.getResource("/krw-people.ttl").getPath
//    loader.loadTurtleFile(peopleFilePath)
//    val placesFilePath = getClass.getResource("/krw-places.ttl").getPath
//    loader.loadTurtleFile(placesFilePath)
//    val residencesFilePath = getClass.getResource("/krw-residences.ttl").getPath
//    loader.loadTurtleFile(residencesFilePath)
//    val thingsFilePath = getClass.getResource("/krw-things.ttl").getPath
//    loader.loadTurtleFile(thingsFilePath)
  }

  test("test4") {
    //val entriesFilePath = getClass.getResource("/J1.E17.xml").getPath
    val entriesFilePath = "/home/rkw/Source/GitHub/hyperdiary/journal-xml/target/J1.E1.xml"
    val model = RDFDataMgr.loadModel(entriesFilePath)
    val out = new PrintWriter(new File("/home/rkw/Source/GitHub/hyperdiary/journal-rdf/turtle/krw/J1.E1.ttl"))
    RDFDataMgr.write(out, model.listStatements().toModel, Lang.TTL)
    out.close()
    assertEquals(true, true)

  }

}
