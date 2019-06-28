package com.example.initial.app.api.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@SuppressWarnings("unused")
public class Greeting {

  @Size(max = 30)
  private String greeting;

  @NotBlank
  @Size(max = 30)
  private String message;

  public Greeting() {}

  public Greeting(String greeting, String message) {
    this.greeting = greeting;
    this.message = message;
  }

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
}
