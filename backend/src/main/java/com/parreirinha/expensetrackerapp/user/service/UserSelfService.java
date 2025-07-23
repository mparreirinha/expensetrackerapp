package com.parreirinha.expensetrackerapp.user.service;

import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;

@Service
public class UserSelfService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserMapper userMapper;
    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    public UserSelfService(
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        TransactionRepository transactionRepository,
        UserMapper userMapper,
        Keycloak keycloakAdminClient
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userMapper = userMapper;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public UserResponseDto getUser(String username) {
        return userMapper.toUserResponseDto(getUserByUsername(username));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = getUserByUsername(username);
        if (!isPasswordValid(username, changePasswordDto.oldPassword()))
            throw new IllegalArgumentException("Invalid Credentials");
        if (changePasswordDto.newPassword().equals(changePasswordDto.oldPassword()))
            throw new IllegalArgumentException("New password must be different from the old password");
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(changePasswordDto.newPassword());
        credential.setTemporary(false);
        RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
        UsersResource usersResource = realmResource.users();
        usersResource.get(user.getKeycloakId()).resetPassword(credential);
    }

    @Transactional
    public void deleteSelf(String username) {
        User user = getUserByUsername(username);
        categoryRepository.deleteByUser(user);
        transactionRepository.deleteByUser(user);
        RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
        realmResource.users().delete(user.getKeycloakId());
        userRepository.delete(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private boolean isPasswordValid(String username, String password) {
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", keycloakClientId);
        params.add("client_secret", keycloakClientSecret);
        params.add("username", username);
        params.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map<String, Object>> response;
        try {
            response = new RestTemplate().exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
    
}
