package com.parreirinha.expensetrackerapp.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parreirinha.expensetrackerapp.auth.service.AuthenticationService;
import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import com.parreirinha.expensetrackerapp.exceptions.EmailAlreadyExistsException;
import com.parreirinha.expensetrackerapp.exceptions.UsernameAlreadyExistsException;
import com.parreirinha.expensetrackerapp.exceptions.RevokedTokenException;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturn201_WhenUserIsCreated() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto(
            "testuser",
            "test@test.com",
            "password"
        );
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserDto)))
            .andExpect(status().isCreated())
            .andExpect(content().string("User created successfully"));
    }

    @Test
    void register_ShouldReturn409_WhenUsernameExists() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto("user", "user@email.com", "pass");
        doThrow(new UsernameAlreadyExistsException("Username already exists"))
            .when(authenticationService).register(any(RegisterUserDto.class));
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserDto)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Username already exists"));
    }

    @Test
    void register_ShouldReturn409_WhenEmailExists() throws Exception {
        RegisterUserDto registerUserDto = new RegisterUserDto("user", "user@email.com", "pass");
        doThrow(new EmailAlreadyExistsException("Email already exists"))
            .when(authenticationService).register(any(RegisterUserDto.class));
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserDto)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Email already exists"));
    }

    @Test
    void register_ShouldReturn400_WhenInvalidInput() throws Exception {
        RegisterUserDto invalidDto = new RegisterUserDto("", "invalid", "");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("Username is required"))
            .andExpect(jsonPath("$.email").value("Invalid email format"))
            .andExpect(jsonPath("$.password").value("Password is required"));
    }

    @Test
    void login_ShouldReturn200_WhenCredentialsAreValid() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto("testuser", "password");
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtService.generateToken(any())).thenReturn("token123");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("token123"))
            .andExpect(jsonPath("$.expiresIn").value(3600000L));
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsAreInvalid() throws Exception {
        LoginUserDto loginUserDto = new LoginUserDto("testuser", "wrongpass");
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenThrow(new BadCredentialsException("Bad credentials"));
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserDto)))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void login_ShouldReturn400_WhenInvalidInput() throws Exception {
        LoginUserDto invalidDto = new LoginUserDto("", "");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("Username is required"))
            .andExpect(jsonPath("$.password").value("Password is required"));
    }

    @Test
    void logout_ShouldReturn200_WhenLogoutSuccessful() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer validtoken"))
            .andExpect(status().isOk())
            .andExpect(content().string("User logged out successfully"));
    }

    @Test
    void logout_ShouldReturn401_WhenTokenRevoked() throws Exception {
        doThrow(new RevokedTokenException("Token is revoked"))
            .when(jwtService).revokeToken(any(String.class));
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer revokedtoken"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string("Token is revoked"));
    }

    @Test
    void logout_ShouldReturn400_WhenTokenFormatInvalid() throws Exception {
        doThrow(new IllegalArgumentException("Invalid token format"))
            .when(jwtService).revokeToken(any(String.class));
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "invalidtoken"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid token format"));
    }
}
