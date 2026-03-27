package com.employee.enrollment.repository;

import com.employee.enrollment.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Employee> findByUsername(String username);

    Optional<Employee> findByEmail(String email);
}
