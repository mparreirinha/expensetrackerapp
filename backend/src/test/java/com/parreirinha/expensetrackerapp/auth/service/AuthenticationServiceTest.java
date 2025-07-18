package com.parreirinha.expensetrackerapp.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import com.parreirinha.expensetrackerapp.exceptions.EmailAlreadyExistsException;
import com.parreirinha.expensetrackerapp.exceptions.UsernameAlreadyExistsException;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    
    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, authenticationManager);
    }

    @Test
    void register_ShouldSaveUSer_WhenDataISValid() {
        // Arrange
        RegisterUserDto dto = new RegisterUserDto("miguel", "miguel@example.com", "password123");
        when(userRepository.existsByUsername("miguel")).thenReturn(false);
        when(userRepository.existsByEmail("miguel@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        // Act
        authenticationService.register(dto);
        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("miguel") &&
            user.getEmail().equals("miguel@example.com") &&
            user.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void register_ShouldThrowUsernameAlreadyExists_WhenUsernameExists() {
        // Arrange
        RegisterUserDto dto = new RegisterUserDto("miguel", "miguel@example.com", "password123");
        when(userRepository.existsByUsername(dto.username())).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.register(dto))
            .isInstanceOf(UsernameAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowEmailAlreadyExists_WhenEmailExists() {
        // Arrange
        RegisterUserDto dto = new RegisterUserDto("miguel", "miguel@example.com", "password123");
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.register(dto))
            .isInstanceOf(EmailAlreadyExistsException.class);
            verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldReturnUserDetails_WhenCredentialsAreValid() {
        // Arrange
        LoginUserDto dto = new LoginUserDto("miguel", "password123");
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        // Act
        UserDetails result = authenticationService.authenticate(dto);
        // Assert
        assertThat(result).isEqualTo(userDetails);
    }

    @Test
    void authenticate_ShouldThrowException_WhenAuthenticationFails() {
        // Arrange
        LoginUserDto dto = new LoginUserDto("miguel", "password123");
        when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Bad credentials"));
        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(dto))
            .isInstanceOf(BadCredentialsException.class);
    }

}
