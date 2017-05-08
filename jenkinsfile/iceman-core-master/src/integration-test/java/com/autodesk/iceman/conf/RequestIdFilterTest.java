package com.autodesk.iceman.conf;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Test;

import com.autodesk.IntegrationTest;
import com.autodesk.iceman.service.EndPoint;
import com.autodesk.service.filter.RequestIdFilter;
import com.autodesk.service.verifier.ApigeeVerifierImpl;

public class RequestIdFilterTest extends IntegrationTest {

  private static final String FEATURE_OF_APPLICATION = EndPoint.FEATURE_OF_APPLICATION.toUrl("applicationId", "featureId");

  @Test
  public void requestIfHeaderHasNoRequestIdThenGenerateOne() {
    given()
        .header(ApigeeVerifierImpl.X_ADS_GATEWAY_SECRET_HEADER, getDefaultApigeeSecret())
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .header(ApigeeVerifierImpl.X_ADS_TOKEN_DATA_HEADER,
            "{\"access_token\":{\"client_id\":\"clientId\"},\"expires_in\":85381,\"client_id\":\"clientId\"}")
        .when().get(FEATURE_OF_APPLICATION).then()
        .header(RequestIdFilter.X_REQUEST_ID_HEADER, not(isEmptyOrNullString()));
  }

  @Test
  public void requestIfHeaderHasRequestIdThenUseIt() {
    final String requestId = "58479cce-b8ac-491f-9eae-c3b71767a444";
    given()
        .header(RequestIdFilter.X_REQUEST_ID_HEADER, requestId)
        .header(ApigeeVerifierImpl.X_ADS_GATEWAY_SECRET_HEADER, getDefaultApigeeSecret())
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .header(ApigeeVerifierImpl.X_ADS_TOKEN_DATA_HEADER,
            "{\"access_token\":{\"client_id\":\"clientId\"},\"expires_in\":85381,\"client_id\":\"clientId\"}")
        .when().get(FEATURE_OF_APPLICATION).then().header(RequestIdFilter.X_REQUEST_ID_HEADER, requestId);
  }

}
