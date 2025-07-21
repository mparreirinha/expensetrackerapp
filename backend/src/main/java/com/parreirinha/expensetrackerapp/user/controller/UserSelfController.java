package com.parreirinha.expensetrackerapp.user.controller;

import org.springframework.web.bind.annotation.*;

import com.parreirinha.expensetrackerapp.auth.service.JwtService;
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
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Self", description = "Operations related to the currently authenticated user")
@RequestMapping("/me")
@RestController
public class UserSelfController {

    private final UserSelfService userSelfService;

    private final JwtService jwtService;

    public UserSelfController(UserSelfService userSelfService, JwtService jwtService) {
        this.userSelfService = userSelfService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Get details of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping()
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponseDto userResponseDto = userSelfService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(userResponseDto);
    }
    
    @Operation(summary = "Change password for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid ChangePasswordDto changePasswordDto, 
                                                 @RequestHeader("Authorization") String token) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        userSelfService.changePassword(userDetails.getUsername(), changePasswordDto);
        jwtService.revokeToken(token);
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Delete the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        userSelfService.deleteSelf(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    
}
