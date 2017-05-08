package com.autodesk.iceman.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class ApplicationTest {

  private Validator validator;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @UseDataProvider(value = "validApplications")
  public void validationSucceeds(String clientId) {
    Application application = new Application();
    application.setClientId(clientId);

    Set<ConstraintViolation<Application>> failures = validator.validate(application);
    assertThat(failures).isEmpty();
  }

  @DataProvider
  public static Object[][] validApplications() {
    // client id
    return new Object[][] {
        { "client-id" }
    };
  }

  @Test
  @UseDataProvider(value = "invalidApplications")
  public void validationFails(String clientId, String property, String message) {
    Application application = new Application();
    application.setClientId(clientId);

    Set<ConstraintViolation<Application>> failures = validator.validate(application);
    assertThat(failures).size().isEqualTo(1);

    ConstraintViolation<Application> failure = failures.iterator().next();
    assertThat(failure.getPropertyPath()).size().isEqualTo(1);
    assertThat(failure.getPropertyPath().iterator().next().getName()).isEqualTo(property);
    assertThat(failure.getMessage()).isEqualTo(message);
  }

  @DataProvider
  public static Object[][] invalidApplications() {
    // client id, expected property, expected message
    return new Object[][] {
        { null, "clientId", "may not be empty" },
        { "", "clientId", "may not be empty" },
        { " ", "clientId", "may not be empty" }
    };
  }

}
