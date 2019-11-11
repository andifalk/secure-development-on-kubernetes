package com.example.initial.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ConfigurationProperties(prefix = "greet")
public class GreetingConfiguration {

  @NotBlank
  @Size(max = 20)
  private String greeting;

  @NotBlank
  @Size(max = 50)
  private String mySec;

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public String getMySec() {
    return mySec;
  }

  public void setMySec(String mySec) {
    this.mySec = mySec;
  }

}
