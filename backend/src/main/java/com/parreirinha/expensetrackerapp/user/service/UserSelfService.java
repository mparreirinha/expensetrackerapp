package com.parreirinha.expensetrackerapp.user.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;

@Service
public class UserSelfService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TokenService tokenService;

    public UserSelfService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        CategoryRepository categoryRepository,
        TransactionRepository transactionRepository,
        TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.tokenService = tokenService;
    }

    public UserResponseDto getUser(String username) {
        return UserMapper.INSTANCE.toUserResponseDto(getUserByUsername(username));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = getUserByUsername(username);
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid Credentials");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteSelf(String username) {
        User user = getUserByUsername(username);
        categoryRepository.deleteByUser(user);
        transactionRepository.deleteByUser(user);
        tokenService.revokeToken(user.getId().toString());
        userRepository.delete(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
}
