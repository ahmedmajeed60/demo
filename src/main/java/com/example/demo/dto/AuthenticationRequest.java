package com.example.demo.dto;

import com.example.demo.annotation.IgnoreWhitespace;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
public class AuthenticationRequest implements Serializable {

    @Email(message = "Email is not valid")
    @Size(min = 2, max = 50)
    @NotNull(message = "Email cannot be null")
    @IgnoreWhitespace(message = "Email is not valid")
    private String email;

    // the regular expression ensures that the password contains at least one lowercase letter,
    // one uppercase letter, one digit, one special character, and is at least 8 characters long.
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password in not valid")
    @Size(min = 8, max = 50)
    @NotNull(message = "Password cannot be null")
    @IgnoreWhitespace(message = "Password is not valid")
    private String password;
}

