package com.parreirinha.expensetrackerapp.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.mapper.CategoryMapper;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

public class CategoryServiceTest {
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private CategoryMapper categoryMapper;
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        categoryService = new CategoryService(categoryRepository, userRepository, transactionRepository, categoryMapper);
    }

    @Test
    void createCategory_ShouldSaveCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        CategoryRequestDto dto = new CategoryRequestDto("Food");
        Category category = new Category();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(categoryMapper.toCategory(dto)).thenReturn(category);
        // Act
        categoryService.createCategory(username, dto);
        // Assert
        verify(categoryRepository).save(category);
    }

    @Test
    void getCategories_ShouldReturnCategoryResponseDtoList() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        List<Category> categories = Arrays.asList(new Category(), new Category());
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<CategoryResponseDto> dtos = Arrays.asList(
            new CategoryResponseDto(id1, "Food"),
            new CategoryResponseDto(id2, "Transport")
        );
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(categoryRepository.findByUser(user)).thenReturn(categories);
        when(categoryMapper.toCategoryResponseDtoList(categories)).thenReturn(dtos);
        // Act
        List<CategoryResponseDto> result = categoryService.getCategories(username);
        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getCategory_ShouldReturnCategoryResponseDto_WhenUserOwnsCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(new CategoryResponseDto(id, "Food"));
        // Act
        CategoryResponseDto result = categoryService.getCategory(username, id);
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void getCategory_ShouldThrowForbiddenException_WhenUserDoesNotOwnCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategory(username, id))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getCategory_ShouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        // Arrange
        String username = "miguel";
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategory(username, id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCategory_ShouldUpdateCategory_WhenUserOwnsCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        CategoryRequestDto dto = new CategoryRequestDto("Updated");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        // Act
        categoryService.updateCategory(id, username, dto);
        // Assert
        verify(categoryRepository).save(category);
        assertThat(category.getName()).isEqualTo("Updated");
    }

    @Test
    void updateCategory_ShouldThrowForbiddenException_WhenUserDoesNotOwnCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        CategoryRequestDto dto = new CategoryRequestDto("Updated");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(id, username, dto))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenUserOwnsCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        // Act
        categoryService.deleteCategory(id, username);
        // Assert
        verify(transactionRepository).unsetCategoryFromTransactions(category);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrowForbiddenException_WhenUserDoesNotOwnCategory() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Category category = new Category(); category.setUser(user);
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(id, username))
            .isInstanceOf(ForbiddenException.class);
    }
} 