package com.example.initial.app;

import com.example.initial.app.config.GreetingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(GreetingConfiguration.class)
@SpringBootApplication
public class InitialSpringBootAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(InitialSpringBootAppApplication.class, args);
  }
}
