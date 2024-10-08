package com.example.demo.dto;

import com.example.demo.annotation.IgnoreWhitespace;
import com.example.demo.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CustomerDto implements Serializable {

    @JsonIgnore
    private Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String customerId;

    @Size(min = 2, max = 20)
    @NotNull(message = "FirstName cannot be null")
    @IgnoreWhitespace(message = "firstName is not valid")
    private String firstName;

    @Size(min = 2, max = 20)
    @NotNull(message = "LastName cannot be null")
    @IgnoreWhitespace(message = "lastName is not valid")
    private String lastName;

    @Email(message = "Email is not valid")
    @Size(min = 10, max = 50)
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

    @NotNull(message = "Role cannot be null")
    private Role role;

    private Boolean active;
}
