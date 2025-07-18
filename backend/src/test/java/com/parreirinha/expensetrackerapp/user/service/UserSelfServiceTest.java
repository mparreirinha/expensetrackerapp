package com.parreirinha.expensetrackerapp.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

public class UserSelfServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private TokenService tokenService;
    private UserMapper userMapper;
    private UserSelfService userSelfService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        categoryRepository = mock(CategoryRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        tokenService = mock(TokenService.class);
        userMapper = mock(UserMapper.class);
        userSelfService = new UserSelfService(userRepository, passwordEncoder, categoryRepository, transactionRepository, tokenService, userMapper);
    }

    @Test
    void getUser_ShouldReturnUserResponseDto_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setUsername("miguel");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        UserResponseDto dto = new UserResponseDto("miguel", "miguel@email.com");
        when(userMapper.toUserResponseDto(user)).thenReturn(dto);
        // Act
        UserResponseDto result = userSelfService.getUser("miguel");
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("miguel");
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userSelfService.getUser("miguel"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenOldPasswordMatchesAndNewIsDifferent() {
        // Arrange
        User user = new User();
        user.setUsername("miguel");
        user.setPassword("oldHash");
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.matches("newPass", "oldHash")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("newHash");
        // Act
        userSelfService.changePassword("miguel", dto);
        // Assert
        assertThat(user.getPassword()).isEqualTo("newHash");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordInvalid() {
        // Arrange
        User user = new User();
        user.setUsername("miguel");
        user.setPassword("oldHash");
        ChangePasswordDto dto = new ChangePasswordDto("wrongOld", "newPass");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "oldHash")).thenReturn(false);
        // Act & Assert
        assertThatThrownBy(() -> userSelfService.changePassword("miguel", dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid Credentials");
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordSameAsOld() {
        // Arrange
        User user = new User();
        user.setUsername("miguel");
        user.setPassword("oldHash");
        ChangePasswordDto dto = new ChangePasswordDto("oldPass", "oldPass");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> userSelfService.changePassword("miguel", dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("New password must be different");
    }

    @Test
    void deleteSelf_ShouldDeleteUserAndRelatedData() {
        // Arrange
        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setUsername("miguel");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        // Act
        userSelfService.deleteSelf("miguel");
        // Assert
        verify(categoryRepository).deleteByUser(user);
        verify(transactionRepository).deleteByUser(user);
        verify(tokenService).revokeToken(user.getId().toString());
        verify(userRepository).delete(user);
    }
} 