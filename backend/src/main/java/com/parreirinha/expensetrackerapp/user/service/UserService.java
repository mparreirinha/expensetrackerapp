package com.parreirinha.expensetrackerapp.user.service;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.service.CategoryService;
import com.parreirinha.expensetrackerapp.transactions.service.TransactionService;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final TokenService tokenService;


    public UserService(UserRepository userRepository,
                       CategoryService categoryService,
                       TransactionService transactionService,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
        this.tokenService = tokenService;
    }

    @Transactional
    public void deleteUser(User user) {
        categoryService.deleteByUser(user);
        transactionService.deleteByUser(user);
        tokenService.revokeToken(user.getId().toString());
        userRepository.delete(user);
    }
}
