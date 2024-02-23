package org.hyperdiary.solid.dpop

import java.net.URI
import java.security.KeyPair

class DPoPManager(keypairs: Map[String,KeyPair]) extends DPoP {

  override def generateProof(algorithm: String, uri: URI, method: String): String = ???

  override def algorithms: List[String] = ???

  override def lookupAlgorithm(jkt: String): Option[String] = ???

  override def lookupThumbprint(algorithm: String): Option[String] = ???
}
