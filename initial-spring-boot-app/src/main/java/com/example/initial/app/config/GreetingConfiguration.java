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
  @Size(max = 20)
  private String message;

  @NotBlank
  @Size(max = 50)
  private String mySec;

  @NotBlank
  @Size(max = 50)
  private String username;

  @NotBlank
  @Size(max = 255)
  private String password;

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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
