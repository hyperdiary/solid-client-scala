package org.hyperdiary.solid.pod

import org.apache.jena.rdf.model.{ Model, Resource }
import org.apache.jena.riot.{ Lang, RDFDataMgr }
import org.hyperdiary.solid.client.Client
import org.hyperdiary.solid.model.Label
import sttp.client3.UriContext
import sttp.model.MediaType.{ ApplicationXml, ImageJpeg }
import sttp.model.{ MediaType, Uri }

import java.io.StringWriter
import java.nio.file.Path
import scala.io.Source
import scala.jdk.CollectionConverters.*
import scala.util.{ Failure, Success, Try, Using }

class PodLoader(client: Client, podUrl: String, baseUrl: Option[String]) {

  // 1) create a container e.g. label/
  def createContainers(containerNames: List[String]): Unit =
    containerNames.foreach(createContainer)

  // 2) create a generated resource with the container e.g. /label/41cf17c4-292d-4d85-b2cf-5513c005d424
  // 3) retrieve the generated URL from the Location header
  def generateResource(containerName: String): Option[String] = {
    val response = client.postResource(uri"$podUrl/$containerName/", "", MediaType("text", "turtle"))
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

  /** For loading Turtle files that make use of fragments or hash URIs , e.g.
    * <http://www.example.org/products#item10245> A Turtle file with fragments should only contain one non-fragment URI
    * as the base URI (e.g.<http://www.example.org/products> or <> ) and then apart from the base subject, all of the
    * other subjects should be fragments (e.g. <#item10245>). This is different to when a Turtle file contains many
    * subjects that are non-fragment URIs as each of these subject will be loaded individually
    */
  def loadTurtleHashFile(turtlePath: String, collectionName: String, localName: String): Unit = {
    val model = RDFDataMgr.loadModel(turtlePath)
    val response =
      client.putResource(uri"$podUrl/$collectionName/$localName", modelAsString(model), MediaType("text", "turtle"))
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  def loadPhoto(photoPath: Path, collectionName: String, localName: String): Unit = {
    val response = client.putResource(uri"$podUrl/$collectionName/$localName", photoPath, ImageJpeg)
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  def loadXml(xmlPath: Path, collectionName: String, localName: String): Unit = {
    val response = client.putResource(uri"$podUrl/$collectionName/$localName", xmlPath, ApplicationXml)
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  private def load(resource: Resource): Unit = {
    val resourceModel = resource.listProperties().toModel
    val uri = resource.getURI.substring(getBaseUrl.length).split("/")
    val collection = uri(0)
    val localName = uri(1)
    val response = if (localName.contains('#')) {
      val hashUri = localName.split("#")
      client.putResource(
        uri"$podUrl/$collection/${hashUri(0)}#${hashUri(1)}",
        modelAsString(resourceModel),
        MediaType("text", "turtle")
      )
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
    val response = client.putContainer(uri"$podUrl/$containerName/")
    response.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

  private def getBaseUrl: String = s"${baseUrl.getOrElse(podUrl)}/"

}
