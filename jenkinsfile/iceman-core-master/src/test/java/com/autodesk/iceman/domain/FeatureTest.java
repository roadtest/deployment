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
public class FeatureTest {

  private Validator validator;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @UseDataProvider(value = "validApplications")
  public void validationSucceeds(String name, Object value) {
    Feature feature = new Feature();
    feature.setName(name);
    feature.setValue(value);

    Set<ConstraintViolation<Feature>> failures = validator.validate(feature);
    assertThat(failures).isEmpty();
  }

  @DataProvider
  public static Object[][] validApplications() {
    // name, value
    return new Object[][] {
        { "name", null },
        { "name", "string" },
        { "name", Boolean.TRUE }
    };
  }

  @Test
  @UseDataProvider(value = "invalidApplications")
  public void validationFails(String name, Object value, String property, String message) {
    Feature feature = new Feature();
    feature.setName(name);
    feature.setValue(value);

    Set<ConstraintViolation<Feature>> failures = validator.validate(feature);
    assertThat(failures).size().isEqualTo(1);

    ConstraintViolation<Feature> failure = failures.iterator().next();
    assertThat(failure.getPropertyPath()).size().isEqualTo(1);
    assertThat(failure.getPropertyPath().iterator().next().getName()).isEqualTo(property);
    assertThat(failure.getMessage()).isEqualTo(message);
  }

  @DataProvider
  public static Object[][] invalidApplications() {
    // name, value, expected property, expected message
    return new Object[][] {
        { null, null, "name", "may not be empty" },
        { "", null, "name", "may not be empty" },
        { " ", null, "name", "may not be empty" }
    };
  }

}
