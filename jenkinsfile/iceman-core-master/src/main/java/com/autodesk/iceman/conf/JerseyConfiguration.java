package com.autodesk.iceman.conf;

import javax.annotation.PostConstruct;
import javax.ws.rs.Priorities;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.autodesk.acm.filter.AcmFilter;
import com.autodesk.service.exception.ApplicationExceptionMapper;
import com.autodesk.service.exception.ConstraintViolationExceptionMapper;
import com.autodesk.service.exception.UnexpectedExceptionMapper;
import com.autodesk.service.exception.WebApplicationExceptionMapper;
import com.autodesk.service.filter.AppToClientFilter;
import com.autodesk.service.filter.AuthenticationFilter;
import com.autodesk.service.filter.ErrorFilter;
import com.autodesk.service.filter.RequestIdFilter;
import com.autodesk.service.filter.ThreadLocalCleanupFilter;
import com.autodesk.iceman.service.v1.ApplicationResource;
import com.autodesk.iceman.service.v1.ApplicationsResource;
import com.autodesk.iceman.service.v1.FeatureOfApplicationResource;
import com.autodesk.iceman.service.v1.FeaturesOfApplicationResource;
import com.autodesk.iceman.service.v1.RootResource;

@Component
public class JerseyConfiguration extends ResourceConfig {

  @Value("${jersey.logging.logger.level}")
  private String loggingLevel;

  @Value("${jersey.logging.verbosity}")
  private String loggingVerbosity;

  @PostConstruct
  public void init() {

    int priority = Priorities.USER;

    // filters
    register(RequestIdFilter.class, ++priority);
    register(AuthenticationFilter.class, ++priority);
    register(AppToClientFilter.class, ++priority);
    register(AcmFilter.class, ++priority);
    register(ErrorFilter.class);
    register(ThreadLocalCleanupFilter.class);

    // exception mappers
    register(ApplicationExceptionMapper.class);
    register(ConstraintViolationExceptionMapper.class);
    register(WebApplicationExceptionMapper.class);
    register(UnexpectedExceptionMapper.class);

    // resources
    register(RootResource.class);
    register(ApplicationsResource.class);
    register(ApplicationResource.class);
    register(FeaturesOfApplicationResource.class);
    register(FeatureOfApplicationResource.class);

    // Jersey wadl
    register(WadlResource.class);

    // Jersey logging
    property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL, loggingLevel);
    property(LoggingFeature.LOGGING_FEATURE_VERBOSITY, loggingVerbosity);
    register(LoggingFeature.class);

  }

}
