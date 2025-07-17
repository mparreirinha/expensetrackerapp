package com.parreirinha.expensetrackerapp.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.parreirinha.expensetrackerapp.auth.dto.LoginResponseDto;
import com.parreirinha.expensetrackerapp.auth.service.AuthenticationService;
import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.user.dto.LoginUserDto;
import com.parreirinha.expensetrackerapp.user.dto.RegisterUserDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Authentication", description = "Register, login and logout endpoints")
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

    @Operation(
        summary = "Register new user",
        description = "Creates a new user account with a unique username and email. Password is stored securely using encryption."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        authenticationService.register(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates the user using provided credentials and returns a JWT token for further authorization."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "403", description = "User account is locked or disabled", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginUserDto loginUserDto) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponseDto);
    }

    @Operation(
        summary = "Logout user",
        description = "Revokes the user's JWT token by invalidating it on the server. Requires a valid token in the Authorization header."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful, token revoked"),
        @ApiResponse(responseCode = "400", description = "Invalid token format", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "Invalid or already revoked token", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        jwtService.revokeToken(token);
        return ResponseEntity.ok("User logged out successfully");
    }
    
    
}
