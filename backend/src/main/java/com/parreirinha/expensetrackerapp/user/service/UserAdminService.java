package com.parreirinha.expensetrackerapp.user.service;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.Role;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TokenService tokenService;

    public UserAdminService(UserRepository userRepository,
                    CategoryRepository categoryRepository,
                    TransactionRepository transactionRepository,
                    TokenService tokenService) {
    
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.tokenService = tokenService;
    }

    public List<UserAdminResponseDto> getUsers() {
        return UserMapper.INSTANCE.toUserAdminResponseDtoList(userRepository.findAll());
    }

    public UserAdminResponseDto getUser(UUID id) {
        return UserMapper.INSTANCE.toUserAdminResponseDto(getUserById(id));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        if (user.getRole() == Role.ADMIN)
            throw new ForbiddenException("Delete Admins is not allowed");
        categoryRepository.deleteByUser(user);
        transactionRepository.deleteByUser(user);
        tokenService.revokeToken(user.getId().toString());
        userRepository.delete(user);
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
