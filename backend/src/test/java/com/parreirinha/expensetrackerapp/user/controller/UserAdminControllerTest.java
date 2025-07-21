package com.parreirinha.expensetrackerapp.user.controller;

import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.config.SecurityConfiguration;
import com.parreirinha.expensetrackerapp.testconfig.NoCsrfTestConfig;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
@Import({SecurityConfiguration.class, NoCsrfTestConfig.class})
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @MockBean 
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUsers_ShouldReturn200_WhenUsersExist() throws Exception {
        // Arrange
        List<UserAdminResponseDto> users = Arrays.asList(
                new UserAdminResponseDto(UUID.randomUUID(), "user1", "user1@email.com", "USER"),
                new UserAdminResponseDto(UUID.randomUUID(), "user2", "user2@email.com", "USER")
        );
        when(userAdminService.getUsers()).thenReturn(users);
        // Act & Assert
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getUsers_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Act & Assert (sem @WithMockUser)
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUser_ShouldReturn200_WhenUserExists() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        UserAdminResponseDto user = new UserAdminResponseDto(id, "user1", "user1@email.com", "USER");
        when(userAdminService.getUser(id)).thenReturn(user);
        // Act & Assert
        mockMvc.perform(get("/admin/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getUser_ShouldReturn401_WhenUserNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userAdminService.getUser(id)).thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        // Act & Assert
        mockMvc.perform(get("/admin/users/" + id))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    void getUser_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(get("/admin/users/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ShouldReturn204_WhenUserDeleted() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // O mock não lança exceção, simula sucesso
        // Act & Assert
        mockMvc.perform(delete("/admin/users/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ShouldReturn403_WhenDeletingAdmin() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new com.parreirinha.expensetrackerapp.exceptions.ForbiddenException("Delete Admins is not allowed"))
                .when(userAdminService).deleteUser(id);
        // Act & Assert
        mockMvc.perform(delete("/admin/users/" + id))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Delete Admins is not allowed"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ShouldReturn401_WhenUserNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"))
                .when(userAdminService).deleteUser(id);
        // Act & Assert
        mockMvc.perform(delete("/admin/users/" + id))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    void deleteUser_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(delete("/admin/users/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Unexpected error"))
                .when(userAdminService).deleteUser(id);
        // Act & Assert
        mockMvc.perform(delete("/admin/users/" + id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }
} 