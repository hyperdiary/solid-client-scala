package org.hyperdiary.solid.pod

import org.apache.jena.rdf.model.{ Model, Resource }
import org.apache.jena.riot.{ Lang, RDFDataMgr }
import org.hyperdiary.solid.client.SolidClient
import org.hyperdiary.solid.model.Label
import sttp.client3.UriContext
import sttp.model.{ MediaType, Uri }

import java.io.StringWriter
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.{ Failure, Success, Try, Using }

class PodLoader(podUrl: String) {

  private val client = new SolidClient()

  // 1) create a container e.g. label/
  def createContainers(containerNames: List[String]): Unit =
    containerNames.foreach(createContainer)

  // 2) create a generated resource with the container e.g. /label/41cf17c4-292d-4d85-b2cf-5513c005d424
  // 3) retrieve the generated URL from the Location header
  def generateResource(containerName: String): Option[String] = {
    val response = client.postResource(uri"$podUrl$containerName/", "", MediaType("text", "turtle"))
    response.body match {
      case Left(body) =>
        println(s"Non-2xx response to GET with code ${response.code}:\n$body")
        None
      case Right(body) =>
        println(s"2xx response to GET:\n$body")
        response.headers.find(_.name == "Location").flatMap(header => Some(header.value))
    }
  }

  def readLabelsCsv(labelCsvPath: String): List[Label] =
    readLines(labelCsvPath) match {
      case Success(lines) =>
        lines.map { line =>
          val cols: Array[String] = line.split("\\|").map(_.trim)
          Label(cols.head, cols(1))
        }
      case Failure(e) =>
        println(s"Failed to read file due to ${e.getMessage}")
        List.empty
    }

  def insertAsSparqlUpdate(resourceUri: Uri, resourceBody: String): Unit = {
    val sparqlInsert = s"INSERT DATA { $resourceBody }"
    val response = client.patchResource(resourceUri, sparqlInsert, MediaType("application", "sparql-update"))
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  def loadTurtleFile(turtlePath: String): Unit = {
    val model = RDFDataMgr.loadModel(turtlePath)
    model.listSubjects().toList.asScala.foreach { resource =>
      load(resource)
    }
  }
  
  def loadTurtleHashFile(turtlePath: String, collectionName: String, localName: String) = {
    val model = RDFDataMgr.loadModel(turtlePath)
    val response = client.putResource(uri"$podUrl/$collectionName/$localName", modelAsString(model), MediaType("text", "turtle"))
    response.body match {
      case Left(body) => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }

  }

  def load(resource: Resource): Unit = {
    val resourceModel = resource.listProperties().toModel
    val uri = resource.getURI.substring(podUrl.length - 1).split("/")
    val collection = uri(0)
    val localName = uri(1)
    val response = if(localName.contains('#')) {
      val hashUri = localName.split("#")
      client.putResource(uri"$podUrl/$collection/${hashUri(0)}#${hashUri(1)}", modelAsString(resourceModel), MediaType("text", "turtle"))
    } else {
      client.putResource(uri"$podUrl/$collection/$localName", modelAsString(resourceModel), MediaType("text", "turtle"))

    }
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  private def modelAsString(model: Model) = {
    val out = new StringWriter()
    RDFDataMgr.write(out, model.listStatements().toModel, Lang.TTL)
    out.toString
  }

  // 4) read labels from CSV file
  private def readLines(labelCsvPath: String): Try[List[String]] =
    Using(Source.fromFile(labelCsvPath)) { bufferedSource =>
      (for (line <- bufferedSource.getLines()) yield line).toList
    }

  // 5) for each label create an RDF resource with type
  // 6) send a sparql-update request to add the resource triples by serializing the RDF resource

  private def createContainer(containerName: String): Unit = {
    val response = client.putContainer(uri"$podUrl$containerName/")
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

}
