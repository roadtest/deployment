package com.autodesk;

import javax.annotation.PostConstruct;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.application.client.id = icemanClientId",
        "spring.application.client.secret = icemanClientSecret",
        "adsk.forge.base.url = http://localhost:8089"
    })
public abstract class IntegrationTest {

  private static final String CLIENT_ID = "icemanClientId";
  private static final String CLIENT_SECRET = "icemanClientSecret";
  private static final String DEFAULT_APIGEE_SECRET = "b4e09b5cc70f469860d8a6aa7f4553c5";  // good for dev only

  @Rule public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(8089));
  @Rule public ExpectedException thrown = ExpectedException.none();

  @LocalServerPort
  private int port;

  @PostConstruct
  private void init() {
    RestAssured.port = port;
  }

  protected String getClientId() {
    return CLIENT_ID;
  }

  protected String getClientSecret() {
    return CLIENT_SECRET;
  }

  protected String getDefaultApigeeSecret() {
    return DEFAULT_APIGEE_SECRET;
  }

}
