package com.parreirinha.expensetrackerapp.user.controller;

import org.springframework.web.bind.annotation.*;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserSelfService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@Tag(name = "Self", description = "Operations related to the currently authenticated user")
@RequestMapping("/me")
@RestController
public class UserSelfController {

    private final UserSelfService userSelfService;

    public UserSelfController(UserSelfService userSelfService) {
        this.userSelfService = userSelfService;
    }

    @GetMapping()
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponseDto userResponseDto = userSelfService.getUser(jwt.getClaimAsString("preferred_username"));
        return ResponseEntity.ok(userResponseDto);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestBody @Valid ChangePasswordDto changePasswordDto, 
                                                 @RequestHeader("Authorization") String token) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        userSelfService.changePassword(jwt.getClaimAsString("preferred_username"), changePasswordDto);
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        userSelfService.deleteSelf(jwt.getClaimAsString("preferred_username"));
        return ResponseEntity.noContent().build();
    }
    
}
