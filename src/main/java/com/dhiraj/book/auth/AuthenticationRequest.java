package com.dhiraj.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email(message = "Email is not well formated..")
    @NotEmpty(message = "Email must be provided")
    @NotBlank(message = "Email must not be blanked ")
    private String email;
    @NotEmpty(message = "Password must be provided")
    @NotBlank(message = "Password must not be blanked ")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
