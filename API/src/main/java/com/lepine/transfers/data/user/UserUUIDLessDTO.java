package com.lepine.transfers.data.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserUUIDLessDTO {
    @Email(message = "{user.email.not_valid}")
    @NotBlank(message = "{user.email.not_blank}")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "{user.password.not_valid}")
    @NotBlank(message = "{user.password.not_blank}")
    private String password;

    @NotBlank(message = "{user.role.not_blank}")
    private String role;
}
