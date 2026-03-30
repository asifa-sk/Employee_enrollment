package com.employee.enrollment;

import com.employee.enrollment.entity.Employee;
import com.employee.enrollment.entity.EmployeeStatus;
import com.employee.enrollment.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class EmployeeEnrollmentApplication {
  public static void main(String[] args) {
    SpringApplication.run(EmployeeEnrollmentApplication.class, args);
  }

  @Bean
  CommandLineRunner seedInitialAdmin(
          EmployeeRepository employeeRepository,
          PasswordEncoder passwordEncoder,
          @Value("${app.bootstrap-admin.enabled:true}") boolean bootstrapEnabled,
          @Value("${app.bootstrap-admin.username:admin}") String username,
          @Value("${app.bootstrap-admin.password:Admin@12345}") String password,
          @Value("${app.bootstrap-admin.email:admin@company.com}") String email,
          @Value("${app.bootstrap-admin.first-name:System}") String firstName,
          @Value("${app.bootstrap-admin.last-name:Administrator}") String lastName,
          @Value("${app.bootstrap-admin.phone:9999999999}") String phone,
          @Value("${app.bootstrap-admin.department:Administration}") String department,
          @Value("${app.bootstrap-admin.salary:0}") Double salary,
          @Value("${app.bootstrap-admin.address:Railway Deployment}") String address) {
    return args -> {
      if (!bootstrapEnabled || employeeRepository.count() > 0) {
        return;
      }

      Employee admin = new Employee();
      admin.setFirstName(firstName);
      admin.setLastName(lastName);
      admin.setEmail(email);
      admin.setPhone(phone);
      admin.setDepartment(department);
      admin.setRole("ADMIN");
      admin.setSalary(salary);
      admin.setJoiningDate(LocalDate.now());
      admin.setAddress(address);
      admin.setUsername(username);
      admin.setPassword(passwordEncoder.encode(password));
      admin.setStatus(EmployeeStatus.ACTIVE);

      employeeRepository.save(admin);
    };
  }
}
