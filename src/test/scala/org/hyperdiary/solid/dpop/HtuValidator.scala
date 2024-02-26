package org.hyperdiary.solid.dpop

import org.jose4j.jwt.consumer.{JwtContext, Validator}

case class HtuValidator (url: String) extends Validator {

  override def validate(jwtContext: JwtContext): String = {
    val claims = jwtContext.getJwtClaims
    if(!claims.hasClaim("htu")) {
      "Missing required htu claim in DPoP token"
    } else if(!url.equalsIgnoreCase(claims.getClaimValueAsString("htu"))) {
      "Incorrect htu claim"
    } else {
      null
    }
  }
}
