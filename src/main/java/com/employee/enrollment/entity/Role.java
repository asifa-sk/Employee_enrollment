package com.employee.enrollment.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    EMPLOYEE;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("ADMIN".equals(normalized)) {
            return ADMIN;
        }
        if ("EMPLOYEE".equals(normalized)) {
            return EMPLOYEE;
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
