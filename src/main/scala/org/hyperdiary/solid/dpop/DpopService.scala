package org.hyperdiary.solid.dpop

import java.security.KeyPair
import java.util

/**
 * A DPoP management abstraction.
 */
trait DpopService {

  /**
   * Create a DPoP manager with a collection of keypairs.
   *
   * @param keypairs the keypairs
   * @return the Dpop manager
   */
  def ofKeyPairs(keypairs: Map[String, KeyPair]): Dpop


}
