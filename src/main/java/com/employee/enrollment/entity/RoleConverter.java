package com.employee.enrollment.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute == Role.ADMIN ? "Admin" : "Employee";
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if ("Admin".equalsIgnoreCase(dbData)) {
            return Role.ADMIN;
        }
        return Role.EMPLOYEE;
    }
}
