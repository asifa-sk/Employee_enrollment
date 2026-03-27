package com.employee.enrollment.dto;

public class LoginResponse {

    private Long employeeId;
    private String username;
    private String role;
    private String status;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long employeeId, String username, String role, String status, String token) {
        this.employeeId = employeeId;
        this.username = username;
        this.role = role;
        this.status = status;
        this.token = token;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
