package com.parreirinha.expensetrackerapp.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.parreirinha.expensetrackerapp.user.domain.User;

@Service
public class UserSelfService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;

    public UserSelfService(
        UserRepository userRepository,
        UserService userService,
        UserQueryService userQueryService,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto getUser(String username) {
        return UserMapper.INSTANCE.toUserResponseDto(userQueryService.getUserByUsername(username));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userQueryService.getUserByUsername(username);
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid Credentials");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteSelf(String username) {
        User user = userQueryService.getUserByUsername(username);
        userService.deleteUser(user);
    }
    
}
