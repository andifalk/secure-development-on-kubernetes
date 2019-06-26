package com.example.initial.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GreetingRestController {

  @GetMapping("/")
  public Greeting greeting(
      @Value("${greeting.prefix:Hello}") String greeting,
      @Value("${greeting.message:world}") String defaultMessage,
      @RequestParam(name = "message", required = false) String message) {
    return new Greeting(greeting, message != null ? message : defaultMessage);
  }
}
