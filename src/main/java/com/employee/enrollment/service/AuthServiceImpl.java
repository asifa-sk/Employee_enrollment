package com.employee.enrollment.service;

import com.employee.enrollment.dto.ForgotPasswordRequest;
import com.employee.enrollment.dto.ForgotPasswordResponse;
import com.employee.enrollment.dto.LoginRequest;
import com.employee.enrollment.dto.LoginResponse;
import com.employee.enrollment.entity.Employee;
import com.employee.enrollment.exception.BadRequestException;
import com.employee.enrollment.repository.EmployeeRepository;
import com.employee.enrollment.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String usernameOrEmail = request.getUsername().trim();
        Employee employee = employeeRepository.findByUsername(usernameOrEmail)
                .or(() -> employeeRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(employee.getUsername(), request.getPassword()));
        } catch (DisabledException ex) {
            throw new BadRequestException("Account is inactive");
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Invalid username or password");
        }

        String token = jwtService.generateToken(new com.employee.enrollment.security.CustomUserDetails(employee));

        return new LoginResponse(
                employee.getId(),
                employee.getUsername(),
                employee.getRole(),
                employee.getStatus().name(),
                token);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String key = request.getUsernameOrEmail().trim();

        Employee employee = employeeRepository.findByUsername(key)
                .or(() -> employeeRepository.findByEmail(key))
                .orElseThrow(() -> new BadRequestException("User not found"));

        String tempPassword = generateTempPassword(10);
        employee.setPassword(passwordEncoder.encode(tempPassword));
        employeeRepository.save(employee);

        return new ForgotPasswordResponse(tempPassword);
    }

    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
