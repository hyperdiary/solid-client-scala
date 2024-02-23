package org.hyperdiary.solid.dpop

import java.security.KeyPair
import java.util

trait DpopService {

  def ofKeyPairs(keypairs: Map[String, KeyPair]): DPoP


}
