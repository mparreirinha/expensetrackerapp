package com.parreirinha.expensetrackerapp.user.service;

import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserMapper userMapper;
    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    public UserAdminService(UserRepository userRepository,
                    CategoryRepository categoryRepository,
                    TransactionRepository transactionRepository,
                    UserMapper userMapper,
                    Keycloak keycloakAdminClient) {
    
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userMapper = userMapper;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public List<UserAdminResponseDto> getUsers() {
        return userMapper.toUserAdminResponseDtoList(userRepository.findAll());
    }

    public UserAdminResponseDto getUser(UUID id) {
        return userMapper.toUserAdminResponseDto(getUserById(id));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        try {
            keycloakAdminClient
                .realm(keycloakRealm)
                .users()
                .get(user.getKeycloakId())
                .remove();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user in Keycloak: " + e.getMessage());
        }
        categoryRepository.deleteByUser(user);
        transactionRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
