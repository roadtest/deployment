package com.autodesk.iceman.service.v1;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class FeatureOfApplicationResourceTest {

  private FeatureOfApplicationResource resource;

  @Before
  public void setup() {
    resource = new FeatureOfApplicationResource();
  }

  @Test
  public void getFeatureOfApplication() {
    Response response = resource.getFeatureOfApplication("client-id", "feature-name");
    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getEntity()).isNotNull();
  }

}
