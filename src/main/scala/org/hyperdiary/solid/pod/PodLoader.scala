package org.hyperdiary.solid.pod

import org.hyperdiary.solid.client.SolidClient
import org.hyperdiary.solid.model.Label
import sttp.client3.UriContext
import sttp.model.MediaType

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

class PodLoader(podUrl: String) {

  private val client = new SolidClient()

  // 1) create a container e.g. label/
  def createContainers(containerNames: List[String]): Unit = {
    containerNames.foreach(createContainer)
  }

  // 2) create a generated resource with the container e.g. /label/41cf17c4-292d-4d85-b2cf-5513c005d424
  // 3) retrieve the generated URL from the Location header
  def generateResource(containerName: String): Option[String] = {
    val response = client.postResource(uri"$podUrl$containerName/","",MediaType("text","turtle"))
    response.body match {
      case Left(body) =>
        println(s"Non-2xx response to GET with code ${response.code}:\n$body")
        None
      case Right(body) =>
        println(s"2xx response to GET:\n$body")
        response.headers.find(_.name == "Location").flatMap(header => Some(header.value))
    }
  }
  
  def getLabels(labelPath: String): List[Label] = {
    readLabelFile(labelPath) match {
      case Success(lines) =>
        lines.map { line =>
          val cols = line.split(";").map(_.trim)
          Label(cols.head, cols(1))
        }
      case Failure(e) =>
        println(s"Failed to read file due to ${e.getMessage}")
        List.empty
    }
    
  }

  // 4) read labels from CSV file
  private def readLabelFile(labelPath: String): Try[List[String]] = {
    Using(Source.fromFile(labelPath)) { bufferedSource =>
      (for (line <- bufferedSource.getLines()) yield line).toList
    }
  }
  
  
  // 5) for each label create an RDF resource with type
  // 6) send a sparql-update request to add the resource triples by serializing the RDF resource

  private def createContainer(containerName: String): Unit = {
    val response = client.putContainer(uri"$podUrl$containerName/")
    response.body match {
      case Left(body) => println(s"Non-2xx response to GET with code ${response.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }
  }

}
