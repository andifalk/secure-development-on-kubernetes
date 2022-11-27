package com.example.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("greeting")
class GreetingAPITests {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  @DisplayName("with message parameter")
  void greetingWithParam() throws Exception {

    mockMvc
        .perform(get("/?message={message}", "Test").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello Test"));
  }

  @Test
  @DisplayName("without message parameter")
  void greetingWithoutParam() throws Exception {

    mockMvc
        .perform(get("/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World"));
  }

  @Test
  @DisplayName("with invalid message")
  void greetingInvalidMessage() throws Exception {

    mockMvc
        .perform(
            get(
                    "/?message={message}",
                    "Testwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("with blank message parameter")
  void greetingEmptyMessage() throws Exception {

    mockMvc
        .perform(get("/?message={message}", " ").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello World"));
  }

  @Test
  @DisplayName("with missing message parameter")
  void greetingNullMessage() throws Exception {

    mockMvc
        .perform(get("/?message=").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
