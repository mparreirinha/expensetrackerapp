package com.parreirinha.expensetrackerapp.user.controller;

import org.springframework.web.bind.annotation.*;

import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserSelfService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


@RequestMapping("/me")
@RestController
public class UserSelfController {

    private final UserSelfService userSelfService;

    private final JwtService jwtService;

    public UserSelfController(UserSelfService userSelfService, JwtService jwtService) {
        this.userSelfService = userSelfService;
        this.jwtService = jwtService;
    }

    @GetMapping()
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userResponseDto = userSelfService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(userResponseDto);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody ChangePasswordDto changePasswordDto, 
                                                 @RequestHeader("Authorization") String token) {
        userSelfService.changePassword(userDetails.getUsername(), changePasswordDto);
        jwtService.revokeToken(token);
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal UserDetails userDetails) {
        userSelfService.deleteSelf(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    
}
