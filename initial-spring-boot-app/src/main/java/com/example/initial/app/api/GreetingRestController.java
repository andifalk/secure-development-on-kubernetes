package com.example.initial.app.api;

import com.example.initial.app.api.resource.Greeting;
import com.example.initial.app.config.GreetingConfiguration;
import org.owasp.encoder.Encode;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GreetingRestController {

  private final GreetingConfiguration greetingConfiguration;

  public GreetingRestController(GreetingConfiguration greetingConfiguration) {
    this.greetingConfiguration = greetingConfiguration;
  }

  @GetMapping("/")
  public Greeting greeting(
      @RequestParam(name = "message", required = false) @Size(max = 30) String message) {
    return new Greeting(
        greetingConfiguration.getGreeting(),
        (message != null && !message.isBlank()) ? Encode.forJavaScript(message) : greetingConfiguration.getMessage());
  }
}
