package com.example.initial.app;

import com.example.initial.app.api.GreetingRestController;
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
class GreetingAPITests {

  @Autowired private MockMvc mockMvc;

  @WithMockUser
  @Test
  void greetingWithGetRequest() throws Exception {

    mockMvc
        .perform(get("/?message={message}", "Test").accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"greeting\":\"Hi\",\"message\":\"Test\"}"));
  }

  @WithMockUser
  @Test
  void greetingWithDefaultGetRequest() throws Exception {

    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"greeting\":\"Hi\",\"message\":\"World\"}"));
  }
}
