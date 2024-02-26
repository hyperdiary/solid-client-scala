package org.hyperdiary.solid.dpop

import org.hyperdiary.solid.common.AuthenticationException
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType.PERMIT
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.EmbeddedJwkVerificationKeyResolver

import java.net.URI
import scala.util.{Failure, Try}

class DpopManagerSuite extends munit.FunSuite {

  private val algorithms = Array[String]("ES256", "RS256")

  test("default DPoP") {
    val dpop = Dpop.apply()
    assertEquals(dpop.algorithms.contains("ES256"), true)
  }

  test("generated proof") {
    val dpop = Dpop.apply()
    val method = "GET"
    val uri = URI.create("https://storage.example/resource")
    val res = for {
      proof  <- dpop.generateProof("ES256", uri, method)
      result <- verifyDpop(proof, uri, method)
    } yield result
    assert(res.isSuccess)
  }

  test("invalid algorithm") {
    val dpop = Dpop.apply()
    val method = "GET"
    val uri = URI.create("https://storage.example/resource")
    dpop.generateProof("RS256", uri, method) match {
      case Failure(_: AuthenticationException) => assert(true)
      case _                                   => fail("Test failed")
    }
  }

  test("look up algorithm") {
    val dpop = Dpop.apply()
    assertEquals(dpop.lookupThumbprint("ES256").flatMap(dpop.lookupAlgorithm), Some("ES256"))
    assertEquals(dpop.lookupThumbprint("RS256").isEmpty, true)
    assertEquals(dpop.lookupThumbprint(null).isEmpty, true)
    assertEquals(dpop.lookupAlgorithm("not-a-thumbprint").isEmpty, true)
    assertEquals(dpop.lookupAlgorithm(null).isEmpty,true)
  }

  private def verifyDpop(proof: String, uri: URI, method: String): Try[Unit] = Try {
    new JwtConsumerBuilder()
      .setRequireJwtId()
      .setExpectedType(true, "dpop+jwt")
      .setJwsAlgorithmConstraints(PERMIT, algorithms: _*)
      .setVerificationKeyResolver(new EmbeddedJwkVerificationKeyResolver)
      .setRequireIssuedAt()
      .setExpectedIssuer(false, null)
      .registerValidator(HtuValidator(uri.toString))
      .registerValidator(HtmValidator(method))
      .build
      .process(proof)
  }

}
