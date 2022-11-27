package com.example.app.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "greet")
public class GreetingConfiguration {

  @NotBlank
  @Size(max = 20)
  private String greeting;

  @NotBlank
  @Size(max = 20)
  private String message;

  @NotBlank
  @Size(max = 50)
  private String mySec;

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMySec() {
    return mySec;
  }

  public void setMySec(String mySec) {
    this.mySec = mySec;
  }
}
