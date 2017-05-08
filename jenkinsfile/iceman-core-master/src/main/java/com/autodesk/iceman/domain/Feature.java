package com.autodesk.iceman.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private Object value;

  @JsonProperty
  @NotBlank
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty
  public Object getValue() {
    return value;
  }

  @JsonIgnore
  public String getValueAsString() {
    if (value instanceof String) {
      return (String) value;
    }
    return value != null ? value.toString() : null;
  }

  @JsonIgnore
  public Boolean getValueAsBoolean() {
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    return value != null ? Boolean.valueOf(value.toString()) : null;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
