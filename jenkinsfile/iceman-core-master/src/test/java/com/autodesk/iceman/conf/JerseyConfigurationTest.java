package com.autodesk.iceman.conf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.logging.Level;

import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class JerseyConfigurationTest {

  @Test
  public void registerLoggingFeatureIfEnvVarConfiguredThenConfiguredValueSet() throws Exception {
    JerseyConfiguration jerseyConfig = new JerseyConfiguration();
    ReflectionTestUtils.setField(jerseyConfig, "loggingLevel", Level.ALL.getName());
    ReflectionTestUtils.setField(jerseyConfig, "loggingVerbosity", LoggingFeature.Verbosity.PAYLOAD_ANY.name());
    jerseyConfig.init();
    assertThat(jerseyConfig.getProperty(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL)).isEqualTo(Level.ALL.getName());
    assertThat(jerseyConfig.getProperty(LoggingFeature.LOGGING_FEATURE_VERBOSITY)).isEqualTo(LoggingFeature.Verbosity.PAYLOAD_ANY.name());
  }

}
