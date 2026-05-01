package org.hyperdiary.solid.model

import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{Lang, RDFDataMgr}

import java.io.StringWriter

trait RdfModel {
  
  def asRdf(resourceUri: String): Model
  
  def asRdfString(resourceUri: String):String = {
    val out = new StringWriter()
    RDFDataMgr.write(out, asRdf(resourceUri), Lang.TTL)
    out.toString
  }
  

}
