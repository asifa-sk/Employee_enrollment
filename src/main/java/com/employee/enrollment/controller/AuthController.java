package com.employee.enrollment.controller;

import com.employee.enrollment.dto.ApiResponse;
import com.employee.enrollment.dto.ChangePasswordRequest;
import com.employee.enrollment.dto.ForgotPasswordRequest;
import com.employee.enrollment.dto.ForgotPasswordResponse;
import com.employee.enrollment.dto.LoginRequest;
import com.employee.enrollment.dto.LoginResponse;
import com.employee.enrollment.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        ApiResponse<LoginResponse> body =
                ApiResponse.of(HttpStatus.OK.value(), "Login successful", response);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = authService.forgotPassword(request);
        ApiResponse<ForgotPasswordResponse> body =
                ApiResponse.of(HttpStatus.OK.value(), "Temporary password generated", response);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        ApiResponse<String> body =
                ApiResponse.of(HttpStatus.OK.value(), "Password changed successfully", "OK");
        return ResponseEntity.ok(body);
    }
}
