package com.employee.enrollment.service;

import com.employee.enrollment.dto.EmployeeEnrollRequest;
import com.employee.enrollment.dto.EmployeeResponse;
import com.employee.enrollment.dto.EmployeeUpdateRequest;
import com.employee.enrollment.entity.Employee;
import com.employee.enrollment.entity.EmployeeStatus;
import com.employee.enrollment.exception.BadRequestException;
import com.employee.enrollment.exception.DuplicateResourceException;
import com.employee.enrollment.exception.ResourceNotFoundException;
import com.employee.enrollment.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeResponse enroll(EmployeeEnrollRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setRole(request.getRole());
        employee.setSalary(request.getSalary());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setAddress(request.getAddress());
        employee.setUsername(request.getUsername());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setStatus(EmployeeStatus.ACTIVE);

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Override
    public EmployeeResponse bootstrapAdmin(EmployeeEnrollRequest request) {
        if (employeeRepository.existsByRoleIgnoreCaseAndStatus("ADMIN", EmployeeStatus.ACTIVE)) {
            throw new BadRequestException("An active admin is already configured");
        }

        request.setRole("ADMIN");
        return enroll(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return toResponse(employee);
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (request.getEmail() != null) {
            if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                    && employeeRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            employee.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            if (!employee.getUsername().equalsIgnoreCase(request.getUsername())
                    && employeeRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            employee.setUsername(request.getUsername());
        }
        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getRole() != null) employee.setRole(request.getRole());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getJoiningDate() != null) employee.setJoiningDate(request.getJoiningDate());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Override
    public EmployeeResponse updateStatus(Long id, EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employee.setStatus(status);
        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employeeRepository.delete(employee);
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setDepartment(employee.getDepartment());
        response.setRole(employee.getRole());
        response.setSalary(employee.getSalary());
        response.setJoiningDate(employee.getJoiningDate());
        response.setAddress(employee.getAddress());
        response.setUsername(employee.getUsername());
        response.setStatus(employee.getStatus());
        response.setCreatedDate(employee.getCreatedDate());
        response.setUpdatedDate(employee.getUpdatedDate());
        return response;
    }
}
