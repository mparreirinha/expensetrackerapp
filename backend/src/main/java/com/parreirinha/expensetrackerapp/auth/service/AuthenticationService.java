package com.parreirinha.expensetrackerapp.auth.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.parreirinha.expensetrackerapp.auth.dto.LoginResponseDto;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final Keycloak keycloakAdminClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    public AuthenticationService(
        UserRepository userRepository,
        Keycloak keycloakAdminClient
    ) {
        this.userRepository = userRepository;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    @Transactional
    public void register(RegisterUserDto registerUserDto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(registerUserDto.username());
        userRepresentation.setEmail(registerUserDto.email());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setRequiredActions(Collections.emptyList());
        userRepresentation.setEnabled(true);
        RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
        UsersResource userResource = realmResource.users();
        Response userResponse = userResource.create(userRepresentation);
        if (userResponse.getStatus() != 201) {
            throw new RuntimeException("Failed to create user on Keycloak: " + userResponse.getStatusInfo().getReasonPhrase());
        }
        String locationPath = userResponse.getLocation().getPath();
        String keycloakId = locationPath.substring(locationPath.lastIndexOf('/') + 1);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerUserDto.password());
        credential.setTemporary(false);
        userResource.get(keycloakId).resetPassword(credential);
        UserResource createdUserResource = userResource.get(keycloakId);
        RoleRepresentation userRole = realmResource.roles().get("USER").toRepresentation();
        createdUserResource.roles().realmLevel().add(List.of(userRole));
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setUsername(registerUserDto.username());
        user.setEmail(registerUserDto.email());
        userRepository.save(user);
    }

    public LoginResponseDto authenticate(LoginUserDto loginUserDto) {
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", keycloakClientId);
        params.add("client_secret", keycloakClientSecret);
        params.add("username", loginUserDto.username());
        params.add("password", loginUserDto.password());
        params.add("scope", "openid");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            tokenUrl,
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        if (!response.getStatusCode().is2xxSuccessful())
            throw new RuntimeException("Failed to authenticate: " + response.getBody());
        Map<String, Object> body = response.getBody();
        String accessToken = (String) body.get("access_token");
        String refreshToken = (String) body.get("refresh_token");
        Integer expiresIn = (Integer) body.get("expires_in");
        if (accessToken == null || accessToken.isBlank())
            throw new RuntimeException("No access token received from Keycloak");
        syncUserFromToken(accessToken);
        return new LoginResponseDto(accessToken, refreshToken, expiresIn);
    }

    private void syncUserFromToken(String accessToken) {
        String url = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> userInfo = response.getBody();
            String keycloakId = (String) userInfo.get("sub");
            String username = (String) userInfo.get("preferred_username");
            String email = (String) userInfo.get("email");
            User user = userRepository.findByKeycloakId(keycloakId).orElseGet(User::new);
            user.setKeycloakId(keycloakId);
            user.setUsername(username);
            user.setEmail(email);
            userRepository.save(user);
        }
    }
    
}
