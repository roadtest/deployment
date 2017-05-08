package com.autodesk.iceman.conf;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

import com.autodesk.IntegrationTest;
import com.autodesk.iceman.service.EndPoint;
import com.autodesk.service.filter.RequestIdFilter;

public class ErrorFilterTest extends IntegrationTest {

  private static final String FEATURE_OF_APPLICATION = EndPoint.FEATURE_OF_APPLICATION.toUrl("applicationId", "featureId");

  @Test
  public void handleApplicationException() throws Exception {
    get(FEATURE_OF_APPLICATION).then()
        .statusCode(401)
        .header(RequestIdFilter.X_REQUEST_ID_HEADER, not(isEmptyOrNullString()))
        .body("statusCode", equalTo(Integer.valueOf(401))).body("time", not(isEmptyOrNullString()))
        .body("requestId", not(isEmptyOrNullString())).body("tenant", equalTo("n/a"))
        .body("user", equalTo("n/a")).body("service", equalTo("iceman"))
        .body("machine", not(isEmptyOrNullString())).body("message", equalTo("Not authorized"));
  }

}
