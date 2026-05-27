package com.rwanda.gov.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private String roles;
    private String message;
    
    public AuthResponse(String token, String email, String roles, String message) {
        this.token = token;
        this.email = email;
        this.roles = roles;
        this.message = message;
    }
}
