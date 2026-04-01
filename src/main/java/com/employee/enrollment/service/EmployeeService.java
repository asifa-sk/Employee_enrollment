package com.employee.enrollment.service;

import com.employee.enrollment.dto.EmployeeEnrollRequest;
import com.employee.enrollment.dto.EmployeeResponse;
import com.employee.enrollment.dto.EmployeeUpdateRequest;
import com.employee.enrollment.entity.EmployeeStatus;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse enroll(EmployeeEnrollRequest request);

    EmployeeResponse bootstrapAdmin(EmployeeEnrollRequest request);

    List<EmployeeResponse> getAll();

    EmployeeResponse getById(Long id);

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    EmployeeResponse updateStatus(Long id, EmployeeStatus status);

    void delete(Long id);
}
