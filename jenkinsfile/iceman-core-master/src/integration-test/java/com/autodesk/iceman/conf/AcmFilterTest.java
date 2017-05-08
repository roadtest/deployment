package com.autodesk.iceman.conf;

import static com.autodesk.acm.mock.AuthorizeMock.ACTION_QUERY_PARAM;
import static com.autodesk.acm.mock.AuthorizeMock.AUTHORIZE_URL_PATTERN;
import static com.autodesk.acm.mock.AuthorizeMock.RESOURCE_QUERY_PARAM;
import static com.autodesk.acm.mock.AuthorizeMock.SUBJECT_QUERY_PARAM;
import static com.autodesk.oauth.mock.AuthenticateMock.AUTHENTICATE_PATH;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.autodesk.IntegrationTest;
import com.autodesk.acm.mock.AuthorizeMock;
import com.autodesk.iceman.service.EndPoint;
import com.autodesk.oauth.mock.AuthenticateMock;
import com.autodesk.oauth.view.Token;
import com.autodesk.service.verifier.ApigeeVerifierImpl;
import com.github.tomakehurst.wiremock.client.WireMock;

public class AcmFilterTest extends IntegrationTest {

  private static final String FEATURE_OF_APPLICATION = EndPoint.FEATURE_OF_APPLICATION.toUrl("applicationId", "featureId");
  private static final String NAMESPACE = "iceman";
  private static final String SUBJECT = "callerClientId";
  private static final String ACTION = "read";
  private static final String RESOURCE = "urn:adsk.iceman:service:app.feature";
  private static final String BEARER_TOKEN = "myToken";

  @Autowired
  private AuthenticateMock authenticateMock;

  @Autowired
  private AuthorizeMock authorizeMock;

  @Before
  public void setup() {
    Token token = new Token();
    token.setAccessToken(BEARER_TOKEN);
    authenticateMock.stubAuthenticate(getClientId(), getClientSecret(), token);
    authorizeMock.stubAuthorize(BEARER_TOKEN, NAMESPACE, SUBJECT, ACTION, RESOURCE);
  }

  @Test
  public void requestAcmAuthorizedResourceThenSucceed() {
    given()
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .header(ApigeeVerifierImpl.X_ADS_GATEWAY_SECRET_HEADER, getDefaultApigeeSecret())
        .header(ApigeeVerifierImpl.X_ADS_TOKEN_DATA_HEADER, "{\"access_token\": {\"client_id\":\"callerClientId\"}, \"client_id\":\"callerClientId\"}")
        .when().get(FEATURE_OF_APPLICATION).then()
        .statusCode(200);

    verify(1, postRequestedFor(urlEqualTo(AUTHENTICATE_PATH)));
    verify(1, getRequestedFor(urlMatching(String.format(AUTHORIZE_URL_PATTERN, NAMESPACE)))
        .withQueryParam(SUBJECT_QUERY_PARAM, WireMock.equalTo(SUBJECT))
        .withQueryParam(ACTION_QUERY_PARAM, WireMock.equalTo(ACTION))
        .withQueryParam(RESOURCE_QUERY_PARAM, WireMock.equalTo(RESOURCE)));
  }

  @Test
  public void requestFromAcmUnauthorizedSubjectThenForbidden() {
    given()
        .header(HttpHeaders.AUTHORIZATION, "Bearer twoLeggedToken")
        .header(ApigeeVerifierImpl.X_ADS_GATEWAY_SECRET_HEADER, getDefaultApigeeSecret())
        .header(ApigeeVerifierImpl.X_ADS_TOKEN_DATA_HEADER, "{\"access_token\": {\"client_id\":\"callerUserId\"}, \"client_id\":\"callerUserId\"}")
        .when().get(FEATURE_OF_APPLICATION).then()
        .statusCode(403)
        .body("statusCode", equalTo(Integer.valueOf(403)))
        .body("message", equalTo("Not authorized"));

    verify(1, postRequestedFor(urlEqualTo(AUTHENTICATE_PATH)));
    verify(1, getRequestedFor(urlMatching(String.format(AUTHORIZE_URL_PATTERN, NAMESPACE)))
        .withQueryParam(SUBJECT_QUERY_PARAM, WireMock.equalTo("callerUserId"))
        .withQueryParam(ACTION_QUERY_PARAM, WireMock.equalTo(ACTION))
        .withQueryParam(RESOURCE_QUERY_PARAM, WireMock.equalTo(RESOURCE)));
  }

}
