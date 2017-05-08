package com.autodesk.iceman.service.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autodesk.iceman.service.Resource.Resources;
import com.autodesk.iceman.service.Version.Versions;

@Path(Versions.V1)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
public class RootResource {

  public static final String ROOT_URN = "urn:adsk.iceman:service";

  @Autowired
  private ApplicationsResource applicationsResource;

  @Path(Resources.APPLICATIONS)
  public ApplicationsResource getApplicationsResource() {
    return applicationsResource;
  }

}
