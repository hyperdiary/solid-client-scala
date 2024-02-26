package org.hyperdiary.solid.dpop

import org.hyperdiary.solid.common.AuthenticationException
import org.jose4j.jwk.PublicJsonWebKey
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.keys.{ EcKeyUtil, EllipticCurves }
import org.jose4j.lang.HashUtil

import java.net.URI
import java.security.KeyPair
import java.util.UUID
import scala.util.{ Failure, Try }

class DpopManager(keypairs: Map[String, KeyPair], thumbprints: Map[String, String]) extends Dpop {

  override def generateProof(algorithm: String, uri: URI, method: String): Try[String] =
    keypairs.get(algorithm) match {
      case Some(keypair) =>
        val result = for {
          jws    <- getJws(keypair, algorithm)
          claims <- getClaims(method, uri)
          _      <- Try(jws.setPayload(claims.toJson))
          res    <- Try(jws.getCompactSerialization)
        } yield res
        println(s"Result:$result") // TODO (RW) remove!
        result
      case None => Failure(AuthenticationException("Unsupported DPoP algorithm: " + algorithm))
    }

  private def getJws(keypair: KeyPair, algorithm: String): Try[JsonWebSignature] = Try {
    val jwk = PublicJsonWebKey.Factory.newPublicJwk(keypair.getPublic)
    val jws = new JsonWebSignature
    jws.setAlgorithmHeaderValue(algorithm)
    jws.setHeader("typ", "dpop+jwt")
    jws.setJwkHeader(jwk)
    jws.setKey(keypair.getPrivate)
    jws
  }

  private def getClaims(method: String, uri: URI): Try[JwtClaims] = Try {
    val claims = new JwtClaims
    claims.setJwtId(UUID.randomUUID.toString)
    claims.setStringClaim("htm", method)
    claims.setStringClaim("htu", uri.toString)
    claims.setIssuedAtToNow()
    claims
  }

  override def algorithms: List[String] = keypairs.keySet.toList

  override def lookupAlgorithm(jkt: String): Option[String] = {
    thumbprints.get(jkt)
  }

  override def lookupThumbprint(algorithm: String): Option[String] = {
    if(algorithm != null) {
      thumbprints.filter(e => algorithm.equals(e._2)).keys.headOption
    } else {
      None
    }
  }
  
}
object DpopManager {
  def apply(): DpopManager = {
    val defaultKeyPair = getDefaultKeyPair
    val thumbprints = getThumbprints(defaultKeyPair)
    new DpopManager(getDefaultKeyPair, thumbprints)
  }

  def apply(keypairs: Map[String, KeyPair]): DpopManager = {
    if (keypairs.isEmpty) {
      val defaultKeyPair = getDefaultKeyPair
      val thumbprints = getThumbprints(defaultKeyPair)
      new DpopManager(getDefaultKeyPair, thumbprints)
    } else {
      val thumbprints = getThumbprints(keypairs)
      new DpopManager(keypairs, thumbprints)
    }
  }

  private def getDefaultKeyPair: Map[String, KeyPair] = {
    val keyUtil = new EcKeyUtil
    Map("ES256" -> keyUtil.generateKeyPair(EllipticCurves.P256))
  }

  private def getThumbprints(keypairs: Map[String,KeyPair]): Map[String, String] =
    keypairs.map { (key, value) =>
      val jwk = PublicJsonWebKey.Factory.newPublicJwk(value.getPublic)
      val jkt = jwk.calculateBase64urlEncodedThumbprint(HashUtil.SHA_256)
      jkt -> key
    }

}
