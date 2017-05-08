package com.autodesk.iceman.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application implements Serializable {

  private static final long serialVersionUID = 1L;

  private String clientId;

  @JsonProperty
  @NotBlank
  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

}
