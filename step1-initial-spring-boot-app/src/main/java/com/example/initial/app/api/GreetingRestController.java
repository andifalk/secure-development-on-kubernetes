package com.example.initial.app.api;

import com.example.initial.app.config.GreetingConfiguration;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Size;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GreetingRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreetingRestController.class);

  private final GreetingConfiguration greetingConfiguration;

  public GreetingRestController(GreetingConfiguration greetingConfiguration) {
    this.greetingConfiguration = greetingConfiguration;
  }

  @GetMapping("/")
  public ResponseEntity<String> greeting(
      @RequestParam(name = "message") @Size(min = 1, max = 50) String message) {
    return ResponseEntity.ok(Encode.forHtml(Encode.forJavaScript(String.format("%s %s", greetingConfiguration.getGreeting(), message))));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> constraintError(ConstraintViolationException ex) {
    LOGGER.error("Constraint validation: " + ex.getMessage(), ex);
    return ResponseEntity.badRequest().body("Invalid parameters provided: " + ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> inputException(MethodArgumentNotValidException ex) {
    LOGGER.error("Method error: " + ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> internalError(RuntimeException ex) {
    LOGGER.error("Internal error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred");
  }
}
