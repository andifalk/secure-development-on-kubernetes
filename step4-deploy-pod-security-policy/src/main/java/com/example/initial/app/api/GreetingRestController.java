package com.example.initial.app.api;

import com.example.initial.app.config.GreetingConfiguration;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GreetingRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreetingRestController.class);

  private final GreetingConfiguration greetingConfiguration;

  public GreetingRestController(GreetingConfiguration greetingConfiguration) {
    this.greetingConfiguration = greetingConfiguration;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/")
  public String greeting(
      @RequestParam(name = "message", required = false) @Pattern(regexp = "[a-zA-Z0-9]{0,30}")
          String message) {
    return String.format(
        "%s %s",
        greetingConfiguration.getGreeting(),
        (message != null && !message.isBlank())
            // ? message // without output escaping
            ? Encode.forHtml(Encode.forJavaScript(message)) // with output escaping
            : greetingConfiguration.getMessage());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public String administration() {
    return "administrative section";
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> constraintError(AccessDeniedException ex) {
    LOGGER.error("Access denied", ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to access this resource");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> constraintError(ConstraintViolationException ex) {
    LOGGER.error("Constraint validation: " + ex.getMessage(), ex);
    return ResponseEntity.badRequest().body("Invalid parameters provided");
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> constraintError(RuntimeException ex) {
    LOGGER.error("Internal error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred");
  }
}
