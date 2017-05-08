package com.autodesk.iceman.service.v1;

import static com.autodesk.iceman.security.AcmAction.READ;
import static com.autodesk.iceman.service.v1.FeatureOfApplicationResource.RESOURCE_URN;
import static com.autodesk.iceman.service.v1.RootResource.ROOT_URN;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;

import com.autodesk.acm.annotation.Acm;
import com.autodesk.iceman.domain.Feature;
import com.autodesk.iceman.service.PathParameter.PathParameters;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@Acm(resource = ROOT_URN + RESOURCE_URN)
public class FeatureOfApplicationResource {

  public static final String RESOURCE_URN = ":app.feature";

  @HystrixCommand
  @GET
  @Acm(actions = READ)
  public Response getFeatureOfApplication(
      @PathParam(PathParameters.CLIENT_ID) @NotBlank String clientId,
      @PathParam(PathParameters.FEATURE_NAME) @NotBlank String featureName) {

    Feature feature = new Feature();
    feature.setName(featureName);
    feature.setValue(Boolean.FALSE);

    return Response.ok(feature).build();
  }

}
