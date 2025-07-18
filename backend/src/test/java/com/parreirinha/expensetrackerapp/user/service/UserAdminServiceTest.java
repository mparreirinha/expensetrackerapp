package com.parreirinha.expensetrackerapp.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.Role;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

public class UserAdminServiceTest {
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private TokenService tokenService;
    private UserMapper userMapper;
    private UserAdminService userAdminService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        tokenService = mock(TokenService.class);
        userMapper = mock(UserMapper.class);
        userAdminService = new UserAdminService(userRepository, categoryRepository, transactionRepository, tokenService, userMapper);
    }

    @Test
    void getUsers_ShouldReturnListOfUserAdminResponseDto() {
        // Arrange
        List<User> users = Arrays.asList(new User(), new User());
        List<UserAdminResponseDto> dtos = Arrays.asList(
            new UserAdminResponseDto(UUID.randomUUID(), "miguel", "miguel@email.com", "USER"),
            new UserAdminResponseDto(UUID.randomUUID(), "john", "john@email.com", "USER")
        );
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserAdminResponseDtoList(users)).thenReturn(dtos);
        // Act
        List<UserAdminResponseDto> result = userAdminService.getUsers();
        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        // Act
        List<UserAdminResponseDto> result = userAdminService.getUsers();
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void getUser_ShouldReturnUserAdminResponseDto_WhenUserExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserAdminResponseDto(user)).thenReturn(new UserAdminResponseDto(id, "miguel", "miguel@email.com", "USER"));
        // Act
        UserAdminResponseDto result = userAdminService.getUser(id);
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userAdminService.getUser(id))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRelatedData_WhenUserIsNotAdmin() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setRole(Role.USER);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        // Act
        userAdminService.deleteUser(id);
        // Assert
        verify(categoryRepository).deleteByUser(user);
        verify(transactionRepository).deleteByUser(user);
        verify(tokenService).revokeToken(user.getId().toString());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowForbiddenException_WhenUserIsAdmin() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setRole(Role.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        // Act & Assert
        assertThatThrownBy(() -> userAdminService.deleteUser(id))
            .isInstanceOf(ForbiddenException.class)
            .hasMessageContaining("Delete Admins is not allowed");
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userAdminService.deleteUser(id))
            .isInstanceOf(UsernameNotFoundException.class);
    }
} 