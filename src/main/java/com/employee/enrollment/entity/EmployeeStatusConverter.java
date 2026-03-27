package com.employee.enrollment.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmployeeStatusConverter implements AttributeConverter<EmployeeStatus, String> {

    @Override
    public String convertToDatabaseColumn(EmployeeStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute == EmployeeStatus.ACTIVE ? "Active" : "Inactive";
    }

    @Override
    public EmployeeStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if ("Active".equalsIgnoreCase(dbData)) {
            return EmployeeStatus.ACTIVE;
        }
        return EmployeeStatus.INACTIVE;
    }
}
