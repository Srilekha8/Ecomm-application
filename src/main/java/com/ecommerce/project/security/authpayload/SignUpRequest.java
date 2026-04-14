package com.ecommerce.project.security.authpayload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min =5, max = 50)
    private String password;

    @NotBlank
    @Size(min =5, max = 50)
    private String username;

    private Set<String> role;
}
