package com.parreirinha.expensetrackerapp.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.service.UserSelfService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/me")
@RestController
public class UserSelfController {

    private final UserSelfService userSelfService;

    public UserSelfController(UserSelfService userSelfService) {
        this.userSelfService = userSelfService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChangePasswordDto changePasswordDto) {
        userSelfService.changePassword(userDetails.getUsername(), changePasswordDto);
        return ResponseEntity.ok("Password changed successfully");
    }
    
    
}
