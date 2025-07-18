package com.parreirinha.expensetrackerapp.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

public class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setUsername("miguel");
        user.setPassword("password");
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.of(user));
        // Act
        UserDetails result = userDetailsService.loadUserByUsername("miguel");
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("miguel");
        verify(userRepository).findByUsername("miguel");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("miguel")).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("miguel"))
            .isInstanceOf(UsernameNotFoundException.class);
        verify(userRepository).findByUsername("miguel");
    }
    
}
