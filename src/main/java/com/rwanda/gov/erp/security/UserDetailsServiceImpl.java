package com.rwanda.gov.erp.security;

import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        if (employee.getStatus() == Employee.EmployeeStatus.DISABLED) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        return UserDetailsImpl.build(employee);
    }
}
