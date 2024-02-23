package org.hyperdiary.solid.dpop
import java.security.KeyPair

class DefaultDpopService extends DpopService {

  override def ofKeyPairs(keypairs: Map[String, KeyPair]): DPoP = new DPoPManager(keypairs)

}
