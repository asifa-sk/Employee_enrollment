package com.employee.enrollment.dto;

public class ForgotPasswordResponse {

    private String temporaryPassword;

    public ForgotPasswordResponse() {
    }

    public ForgotPasswordResponse(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }
}
