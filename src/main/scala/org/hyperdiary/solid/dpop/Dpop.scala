package org.hyperdiary.solid.dpop

import java.net.URI
import java.security.KeyPair
trait DPoP {

  def generateProof(algorithm: String, uri: URI, method: String): String

  def algorithms: List[String]

  def lookupAlgorithm(jkt: String): Option[String]

  def lookupThumbprint(algorithm: String): Option[String]

  def of: DPoP = of(Map.empty)

  def of(keypairs: Map[String, KeyPair]): DPoP = DefaultDpopService().ofKeyPairs(keypairs)


}
