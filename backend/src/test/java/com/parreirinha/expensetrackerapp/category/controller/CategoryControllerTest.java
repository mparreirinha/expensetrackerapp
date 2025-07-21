package com.parreirinha.expensetrackerapp.category.controller;

import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.service.CategoryService;
import com.parreirinha.expensetrackerapp.config.SecurityConfiguration;
import com.parreirinha.expensetrackerapp.testconfig.NoCsrfTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({SecurityConfiguration.class, NoCsrfTestConfig.class})
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;
    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCategories_ShouldReturn200_WhenCategoriesExist() throws Exception {
        // Arrange
        List<CategoryResponseDto> categories = Arrays.asList(
                new CategoryResponseDto(UUID.randomUUID(), "Food"),
                new CategoryResponseDto(UUID.randomUUID(), "Transport")
        );
        when(categoryService.getCategories("user")).thenReturn(categories);
        // Act & Assert
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[1].name").value("Transport"));
    }

    @Test
    void getCategories_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCategory_ShouldReturn200_WhenCategoryExists() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryResponseDto dto = new CategoryResponseDto(id, "Food");
        when(categoryService.getCategory("user", id)).thenReturn(dto);
        // Act & Assert
        mockMvc.perform(get("/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Food"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getCategory_ShouldReturn401_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryService.getCategory("user", id)).thenThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Category not found"));
        // Act & Assert
        mockMvc.perform(get("/categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void getCategory_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(get("/categories/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createCategory_ShouldReturn201_WhenCreated() throws Exception {
        // Arrange
        CategoryRequestDto req = new CategoryRequestDto("Food");
        // Act & Assert
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createCategory_ShouldReturn400_WhenInvalid() throws Exception {
        // Arrange
        CategoryRequestDto req = new CategoryRequestDto(""); // nome inválido
        doThrow(new IllegalArgumentException("Invalid category name")).when(categoryService).createCategory(Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"name\":\"Name is required\"}"));
    }

    @Test
    void createCategory_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        CategoryRequestDto req = new CategoryRequestDto("Food");
        // Act & Assert
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateCategory_ShouldReturn200_WhenUpdated() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequestDto req = new CategoryRequestDto("Updated");
        // Act & Assert
        mockMvc.perform(put("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateCategory_ShouldReturn400_WhenInvalid() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequestDto req = new CategoryRequestDto("");
        doThrow(new IllegalArgumentException("Invalid category name")).when(categoryService).updateCategory(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"name\":\"Name is required\"}"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateCategory_ShouldReturn401_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequestDto req = new CategoryRequestDto("Updated");
        doThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Category not found")).when(categoryService).updateCategory(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void updateCategory_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequestDto req = new CategoryRequestDto("Updated");
        // Act & Assert
        mockMvc.perform(put("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateCategory_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequestDto req = new CategoryRequestDto("Updated");
        doThrow(new RuntimeException("Unexpected error")).when(categoryService).updateCategory(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteCategory_ShouldReturn204_WhenDeleted() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(delete("/categories/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteCategory_ShouldReturn401_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Category not found")).when(categoryService).deleteCategory(id, "user");
        // Act & Assert
        mockMvc.perform(delete("/categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void deleteCategory_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(delete("/categories/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteCategory_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Unexpected error")).when(categoryService).deleteCategory(id, "user");
        // Act & Assert
        mockMvc.perform(delete("/categories/" + id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }
} 