package org.hyperdiary.solid.model

import org.apache.jena.rdf.model.{Model, ModelFactory, Resource}
import org.apache.jena.vocabulary.{RDF, RDFS}

case class Label(labelText: String, labelTargetUri: String) extends RdfModel {
  override def asRdf(resourceUri:String): Model = {
    val model = ModelFactory.createDefaultModel()
    val labelResource = model.createResource(resourceUri)
    val hyperdiaryLabel = model.createResource("http://hyperdiary.io/terms/Label")
    val isLabelFor = model.createProperty("http://hyperdiary.io/terms/", "isLabelFor")
    val labelTarget = model.createResource(labelTargetUri)
    labelResource.addProperty(RDF.`type`, hyperdiaryLabel)
    labelResource.addProperty(RDFS.label, labelText)
    labelResource.addProperty(isLabelFor, labelTarget)
    model
  }
}
