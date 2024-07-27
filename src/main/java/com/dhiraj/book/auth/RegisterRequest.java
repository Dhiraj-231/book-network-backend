package com.dhiraj.book.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotEmpty(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotEmpty(message = "Last name is mandatory")
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @Email(message = "Email is not well formated..")
    @NotEmpty(message = "Email must be provided")
    @NotBlank(message = "Email must not be blanked ")
    private String email;
    @NotEmpty(message = "Password must be provided")
    @NotBlank(message = "Password must not be blanked ")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
