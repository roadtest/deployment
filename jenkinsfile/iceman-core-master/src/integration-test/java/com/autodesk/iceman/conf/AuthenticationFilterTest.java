package com.autodesk.iceman.conf;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Test;

import com.autodesk.IntegrationTest;
import com.autodesk.iceman.service.EndPoint;
import com.autodesk.service.verifier.ApigeeVerifierImpl;

public class AuthenticationFilterTest extends IntegrationTest {

  private static final String FEATURE_OF_APPLICATION = EndPoint.FEATURE_OF_APPLICATION.toUrl("applicationId", "featureId");

  @Test
  public void requestIfNoAuthorizationHeaderThenNotAuthorized() {
    given()
        .when().get(FEATURE_OF_APPLICATION).then()
            .statusCode(401)
            .body("statusCode", equalTo(Integer.valueOf(401)))
            .body("message", equalTo("Not authorized"));
  }

  @Test
  public void requestIfNoTokenDataHeaderThenNotAuthorized() {
    given()
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .header(ApigeeVerifierImpl.X_ADS_GATEWAY_SECRET_HEADER, getDefaultApigeeSecret())
        .when().get(FEATURE_OF_APPLICATION).then()
            .statusCode(401)
            .body("statusCode", equalTo(Integer.valueOf(401)))
            .body("message", equalTo("Not authorized"));
  }

}
