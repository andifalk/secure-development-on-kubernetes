package com.example.initial.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final GreetingConfiguration greetingConfiguration;

  @Autowired
  public WebSecurityConfiguration(GreetingConfiguration greetingConfiguration) {
    this.greetingConfiguration = greetingConfiguration;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // http.headers().xssProtection().xssProtectionEnabled(false);

    http.authorizeRequests()
        .requestMatchers(EndpointRequest.to(HealthEndpoint.class))
        .permitAll()
        .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .and()
        .httpBasic();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
        User.withUsername(greetingConfiguration.getUsername())
            .password(passwordEncoder().encode(greetingConfiguration.getPassword()))
            .roles("USER")
            .build(),
        User.withUsername(greetingConfiguration.getAdminUsername())
            .password(passwordEncoder().encode(greetingConfiguration.getAdminPassword()))
            .roles("USER", "ADMIN")
            .build());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
