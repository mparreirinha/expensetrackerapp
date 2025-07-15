package com.parreirinha.expensetrackerapp.user.service;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    public UserSelfService(
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        TransactionRepository transactionRepository,
        TokenService tokenService,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserMapper.INSTANCE.toUserResponseDto(user);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid Credentials");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteSelf(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        categoryRepository.deleteByUser(user);
        transactionRepository.deleteByUser(user);
        tokenService.revokeToken(user.getId().toString());
        userRepository.delete(user);
    }
    
}
