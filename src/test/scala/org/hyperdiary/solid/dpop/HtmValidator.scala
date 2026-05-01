package org.hyperdiary.solid.dpop

import org.jose4j.jwt.consumer.{JwtContext, Validator}

case class HtmValidator (method: String) extends Validator {

  override def validate(jwtContext: JwtContext): String = {
    val claims = jwtContext.getJwtClaims
    if(!claims.hasClaim("htm")) {
      "Missing required htm claim in DPoP token"
    } else if(!method.equalsIgnoreCase(claims.getClaimValueAsString("htm"))){
      "Incorrect htm claim"
    } else {
      null
    }
  }
}
