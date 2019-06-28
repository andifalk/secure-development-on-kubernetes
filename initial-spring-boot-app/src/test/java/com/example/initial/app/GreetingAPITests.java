package com.example.initial.app;

import com.example.initial.app.api.GreetingRestController;
import com.example.initial.app.api.resource.Greeting;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GreetingRestController.class)
public class GreetingAPITests {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @WithMockUser
  @Test
  public void greetingWithGetRequest() throws Exception {

    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string("{\"greeting\":\"Hi\",\"message\":\"World\"}"));
  }

  @WithMockUser
  @Test
  public void greetingWithPostRequest() throws Exception {

    mockMvc
            .perform(post("/").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(new Greeting("Hi", "Test"))))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"greeting\":\"Hi\",\"message\":\"Test\"}"));
  }

}
