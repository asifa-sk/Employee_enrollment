CREATE DATABASE IF NOT EXISTS employee_enrollment;
USE employee_enrollment;

CREATE TABLE employee (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    joining_date DATE NOT NULL,
    address TEXT NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_salary_positive CHECK (salary > 0),
    CONSTRAINT chk_phone_valid CHECK (phone REGEXP '^[0-9]{10}$'),
    CONSTRAINT chk_joining_date CHECK (joining_date <= CURRENT_DATE),
    CONSTRAINT chk_role_valid CHECK (role IN ('Admin','Employee')),
    CONSTRAINT chk_email_format CHECK (email LIKE '%_@_%._%'),
    CONSTRAINT chk_status_valid CHECK (status IN ('Active','Inactive'))
);

CREATE INDEX idx_employee_email ON employee(email);
CREATE INDEX idx_employee_username ON employee(username);
CREATE INDEX idx_employee_department ON employee(department);
CREATE INDEX idx_employee_status ON employee(status);

INSERT INTO employee (
    first_name,
    last_name,
    email,
    phone,
    department,
    role,
    salary,
    joining_date,
    address,
    username,
    password,
    status
) VALUES (
    'Admin',
    'User',
    'admin@company.com',
    '1234567890',
    'IT',
    'Admin',
    100000.00,
    CURDATE(),
    'Company Address',
    'admin',
    '$2a$10$li.SOjhB//DzrMP73sGMAeZZRUGa6aiMgwfFxFLm.KYbnIOjILkEO',
    'Active'
);

DESCRIBE employee;
