package com.example.initial.app;

import com.example.initial.app.api.GreetingRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GreetingRestController.class)
@DisplayName("greeting")
class GreetingAPITests {

  @Autowired private MockMvc mockMvc;

  @WithMockUser
  @Test
  @DisplayName("with default message")
  void greetingWithDefaultGetRequest() throws Exception {

    mockMvc
        .perform(get("/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Hi World"));
  }

  @WithMockUser
  @Test
  @DisplayName("with custom message")
  void greetingWithGetRequest() throws Exception {

    mockMvc
        .perform(get("/?message={message}", "Test").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Hi Test"));
  }

  @Test
  @DisplayName("not accessible without authentication")
  void greetingWithGetRequestUnauthorized() throws Exception {

    mockMvc
        .perform(get("/?message={message}", "Test").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @WithMockUser(roles = "ADMIN")
  @Test
  @DisplayName("admin section")
  void adminSectionWithGetRequest() throws Exception {

    mockMvc
        .perform(get("/admin").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("administrative section"));
  }

  @WithMockUser
  @Test
  @DisplayName("admin section not accessible without admin role")
  void adminSectionWithGetRequestAccessDenied() throws Exception {

    mockMvc
        .perform(get("/admin").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
