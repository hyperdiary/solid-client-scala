package org.hyperdiary.solid.dpop
import java.security.KeyPair

class DefaultDpopService extends DpopService {

  override def ofKeyPairs(keypairs: Map[String, KeyPair]): Dpop = DpopManager(keypairs)

}
