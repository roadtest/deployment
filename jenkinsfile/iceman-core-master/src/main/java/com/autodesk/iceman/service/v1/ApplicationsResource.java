package com.autodesk.iceman.service.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autodesk.iceman.service.PathParameter.PathParameters;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class ApplicationsResource {

  @Autowired
  private ApplicationResource applicationResource;

  @Path("{" + PathParameters.CLIENT_ID + "}")
  public ApplicationResource getApplicationResource() {
    return applicationResource;
  }

}
