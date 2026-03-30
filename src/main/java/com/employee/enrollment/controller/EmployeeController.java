package com.employee.enrollment.controller;

import com.employee.enrollment.dto.ApiResponse;
import com.employee.enrollment.dto.EmployeeEnrollRequest;
import com.employee.enrollment.dto.EmployeeResponse;
import com.employee.enrollment.dto.EmployeeUpdateRequest;
import com.employee.enrollment.dto.StatusUpdateRequest;
import com.employee.enrollment.entity.EmployeeStatus;
import com.employee.enrollment.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/bootstrap-admin")
    public ResponseEntity<ApiResponse<EmployeeResponse>> bootstrapAdmin(@Valid @RequestBody EmployeeEnrollRequest request) {
        EmployeeResponse response = employeeService.bootstrapAdmin(request);
        ApiResponse<EmployeeResponse> body =
                ApiResponse.of(HttpStatus.CREATED.value(), "Initial admin created successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> enroll(@Valid @RequestBody EmployeeEnrollRequest request) {
        EmployeeResponse response = employeeService.enroll(request);
        ApiResponse<EmployeeResponse> body =
                ApiResponse.of(HttpStatus.CREATED.value(), "Employee enrolled successfully", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAll() {
        List<EmployeeResponse> response = employeeService.getAll();
        ApiResponse<List<EmployeeResponse>> body =
                ApiResponse.of(HttpStatus.OK.value(), "Employees fetched successfully", response);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getById(id);
        ApiResponse<EmployeeResponse> body =
                ApiResponse.of(HttpStatus.OK.value(), "Employee fetched successfully", response);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody EmployeeUpdateRequest request) {
        EmployeeResponse response = employeeService.update(id, request);
        ApiResponse<EmployeeResponse> body =
                ApiResponse.of(HttpStatus.OK.value(), "Employee updated successfully", response);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateStatus(@PathVariable Long id,
                                                                      @Valid @RequestBody StatusUpdateRequest request) {
        EmployeeStatus status = request.getStatus();
        EmployeeResponse response = employeeService.updateStatus(id, status);
        ApiResponse<EmployeeResponse> body =
                ApiResponse.of(HttpStatus.OK.value(), "Employee status updated successfully", response);
        return ResponseEntity.ok(body);
    }
}
