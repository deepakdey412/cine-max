package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponseDto;
import com.moviebooking.dto.JwtResponse;
import com.moviebooking.dto.LoginRequest;
import com.moviebooking.dto.RegisterRequest;
import com.moviebooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Create a new user account with USER role. Returns JWT token upon successful registration."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input or username/email already exists"),
        @ApiResponse(responseCode = "422", description = "Validation error - Check request body fields")
    })
    public ResponseEntity<ApiResponseDto> register(
        @Valid 
        @RequestBody(description = "User registration details", required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class)))
        @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponseDto.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticate user with username and password. Returns JWT token for subsequent API calls."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Missing required fields")
    })
    public ResponseEntity<ApiResponseDto> login(
        @Valid 
        @RequestBody(description = "User login credentials", required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class)))
        @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponseDto.success("Login successful", response));
    }
}
