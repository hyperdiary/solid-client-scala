package org.hyperdiary.solid.dpop

import java.net.URI
import java.security.KeyPair
import scala.util.Try

/**
 * An abstraction for working with OAuth 2.0 Demonstrating Proof-of-Possession at the Application Layer (DPoP).
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-ietf-oauth-dpop">DPoP draft specification</a>
 */
trait Dpop {

  /**
   * Generate a DPoP proof for a given URI and method pair.
   * @param algorithm the algorithm to use
   * @param uri       the HTTP URI
   * @param method    the HTTP method
   * @return the DPoP Proof, serialized as a Base64-encoded string, suitable for use with HTTP headers
   */
  def generateProof(algorithm: String, uri: URI, method: String): Try[String]

  /**
   * Return a collection of the supported algorithm names.
   * @return the algorithm names
   */
  def algorithms: List[String]

  /**
   * Retrieve the algorithm for the given thumbprint, if available.
   * @param jkt the JSON Key Thumbprint
   * @return the algorithm, if present
   */
  def lookupAlgorithm(jkt: String): Option[String]

  /**
   * Retrieve the thumbprint for a given algorithm, if available.
   * @param algorithm the algorithm
   * @return the thumbprint, if present
   */
  def lookupThumbprint(algorithm: String): Option[String]

  


}
object Dpop {
  
  /**
   * Create a DPoP manager that supports a default keypair.
   * @return the DPoP manager
   */
  def apply(): Dpop = apply(Map.empty)

  /**
   * Create a DPoP manager that supports some number of keypairs.
   * @param keypairs the keypairs
   * @return the DPoP manager
   */
  def apply(keypairs: Map[String, KeyPair]): Dpop = DefaultDpopService().ofKeyPairs(keypairs)

}
