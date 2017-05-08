package com.autodesk.iceman.service.v1;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Ignore;
import org.junit.Test;

import com.autodesk.IntegrationTest;

public class FeatureOfApplicationResourceIntTest extends IntegrationTest {

  @Test
  @Ignore  // TODO: need service mocking first
  public void getFeatureOfApplication() {
    given()
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .when().get("/v1/applications/client-id/features/feature-name").then()
            .statusCode(200)
            .body("name", equalTo("feature-name"))
            .body("value", equalTo(Boolean.FALSE));
  }

}
