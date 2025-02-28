package com.parreirinha.expensetrackerapp.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.parreirinha.expensetrackerapp.auth.dto.LoginResponseDto;
import com.parreirinha.expensetrackerapp.auth.service.AuthenticationService;
import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(
        JwtService jwtService,
        AuthenticationService authenticationService
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {
        authenticationService.register(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserDto loginUserDto) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponseDto);
    }
    
}
