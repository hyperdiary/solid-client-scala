package org.hyperdiary.solid.model

import org.apache.jena.rdf.model.{ ModelFactory, Resource }
import org.apache.jena.vocabulary.{ RDF, RDFS }

case class Label(labelText: String, labelTargetUri: String) {
  def asRdf(labelUri:String): Resource = {
    val model = ModelFactory.createDefaultModel()
    val labelResource = model.createResource(labelUri)
    val hyperdiaryLabel = model.createResource("http://hyperdiary.io/terms/Label")
    val isLabelFor = model.createProperty("http://hyperdiary.io/terms/", "isLabelFor")
    val labelTarget = model.createResource(labelTargetUri)
    labelResource.addProperty(RDF.`type`, hyperdiaryLabel)
    labelResource.addProperty(RDFS.label, labelText)
    labelResource.addProperty(isLabelFor, labelTarget)
    labelResource
  }
}
