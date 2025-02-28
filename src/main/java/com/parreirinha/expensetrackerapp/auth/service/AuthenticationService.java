package com.parreirinha.expensetrackerapp.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.parreirinha.expensetrackerapp.exceptions.EmailAlreadyExistsException;
import com.parreirinha.expensetrackerapp.exceptions.UsernameAlreadyExistsException;
import com.parreirinha.expensetrackerapp.user.domain.Role;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterUserDto registerUserDto) {
        if (userRepository.existsByUsername(registerUserDto.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(registerUserDto.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setUsername(registerUserDto.username());
        user.setEmail(registerUserDto.email());
        user.setPassword(passwordEncoder.encode(registerUserDto.password()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public UserDetails authenticate(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUserDto.username(), 
                loginUserDto.password())
        );
        return userRepository.findByUsername(loginUserDto.username())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
}
