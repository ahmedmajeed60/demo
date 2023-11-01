package com.example.demo;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.util.Constant;
import com.example.demo.util.TestConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAuthenticationWithValidCredentials() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.VALID_EMAIL, TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testAuthenticationWithInValidCredentials() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.INVALID_EMAIL, TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

    }

    @Test
    void testAuthenticationWithInValidEmailPattern() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("dummy password", TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("Email is not valid")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithInValidEmailLessThanMinSize() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("a@g.com", TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("size must be between 10 and 50")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithInValidEmailMoreThanMaxSize() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("dummydummydummydummydummydummydummydummydummy@google.com",
                        TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("size must be between 10 and 50")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithNullEmail() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(null, TestConstant.VALID_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("Email cannot be null")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithInValidPasswordPattern() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.VALID_EMAIL, "dummy password");

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("Password in not valid")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithInValidPasswordLessThanMinSize() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.VALID_EMAIL, "dummy");

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("size must be between 8 and 50")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithInValidPasswordMoreThanMaxSize() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.VALID_EMAIL,
                        "dummyDummy@123dummyDummy@123dummyDummy@123dummyDummy@123dummyDummy@123");

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("size must be between 8 and 50")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void testAuthenticationWithNullPassword() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(TestConstant.VALID_EMAIL, null);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(Constant.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value(Matchers.containsString("Password cannot be null")))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }
}
