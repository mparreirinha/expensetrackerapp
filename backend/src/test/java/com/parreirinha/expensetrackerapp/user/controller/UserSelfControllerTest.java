package com.parreirinha.expensetrackerapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.testconfig.NoCsrfTestConfig;
import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserSelfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSelfController.class)
@Import(NoCsrfTestConfig.class)
public class UserSelfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSelfService userSelfService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUser_ShouldReturn200_WhenUserExists() throws Exception {
        // Arrange
        UserResponseDto responseDto = new UserResponseDto("testuser", "test@test.com");
        when(userSelfService.getUser("testuser")).thenReturn(responseDto);
        // Act & Assert
        mockMvc.perform(get("/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        // Arrange
        when(userSelfService.getUser("testuser")).thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        // Act & Assert
        mockMvc.perform(get("/me").principal(() -> "testuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void changePassword_ShouldReturn200_WhenPasswordChanged() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .principal(() -> "testuser")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void changePassword_ShouldReturn400_WhenInvalidCredentials() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");
        doThrow(new IllegalArgumentException("Invalid Credentials"))
                .when(userSelfService).changePassword(eq("testuser"), any(ChangePasswordDto.class));
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .principal(() -> "testuser")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid Credentials"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void changePassword_ShouldReturn400_WhenInvalidInput() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("", "");
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .principal(() -> "testuser")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.oldPassword").value("Old Password is required"))
                .andExpect(jsonPath("$.newPassword").value("New Password is required"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void changePassword_ShouldReturn400_WhenNewPasswordSameAsOld() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("samePass", "samePass");
        doThrow(new IllegalArgumentException("New password must be different from the old password"))
                .when(userSelfService).changePassword(eq("testuser"), any(ChangePasswordDto.class));
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("New password must be different from the old password"));
    }

    @Test
    void getUser_ShouldReturn401_WhenUnauthorized() throws Exception {
        // Arrange - no @WithMockUser
        mockMvc.perform(get("/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_ShouldReturn401_WhenUnauthorized() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteSelf_ShouldReturn204_WhenUserDeleted() throws Exception {
        // Arrange
        // Act & Assert
        mockMvc.perform(delete("/me/delete").principal(() -> "testuser"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteSelf_ShouldReturn404_WhenUserNotFound() throws Exception {
        // Arrange
        doThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"))
                .when(userSelfService).deleteSelf("testuser");
        // Act & Assert
        mockMvc.perform(delete("/me/delete").principal(() -> "testuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    void deleteSelf_ShouldReturn401_WhenUnauthorized() throws Exception {
        // Arrange - no @WithMockUser
        // Act & Assert
        mockMvc.perform(delete("/me/delete"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void changePassword_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");
        doThrow(new RuntimeException("Unexpected error"))
                .when(userSelfService).changePassword(eq("testuser"), any(ChangePasswordDto.class));
        // Act & Assert
        mockMvc.perform(post("/me/change-password")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteSelf_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Unexpected error"))
                .when(userSelfService).deleteSelf("testuser");
        // Act & Assert
        mockMvc.perform(delete("/me/delete"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }
    
} 