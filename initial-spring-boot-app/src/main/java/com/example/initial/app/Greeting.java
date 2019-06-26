package com.example.initial.app;

public class Greeting {

  private String greeting;
  private String message;

  public Greeting() {
  }

  Greeting(String greeting, String message) {
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
