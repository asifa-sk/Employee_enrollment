package com.employee.enrollment.security;

import com.employee.enrollment.entity.Employee;
import com.employee.enrollment.entity.EmployeeStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Employee employee) {
        this.id = employee.getId();
        this.username = employee.getUsername();
        this.password = employee.getPassword();
        this.enabled = employee.getStatus() == EmployeeStatus.ACTIVE;
        String role = employee.getRole();
        if (role == null || role.trim().isEmpty()) {
            this.authorities = List.of();
        } else {
            String normalized = role.trim().toUpperCase().replaceAll("\\s+", "_");
            this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + normalized));
        }
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
