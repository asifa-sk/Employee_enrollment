package com.employee.enrollment.service;

import com.employee.enrollment.dto.ForgotPasswordRequest;
import com.employee.enrollment.dto.ForgotPasswordResponse;
import com.employee.enrollment.dto.LoginRequest;
import com.employee.enrollment.dto.LoginResponse;
import com.employee.enrollment.dto.ChangePasswordRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    void changePassword(String username, ChangePasswordRequest request);
}
