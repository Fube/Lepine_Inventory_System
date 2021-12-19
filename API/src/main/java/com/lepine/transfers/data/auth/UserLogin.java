package com.lepine.transfers.data.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserLogin {

    @NotBlank(message = "{user.email.not_blank}")
    private String email;

    @NotBlank(message = "{user.password.not_blank}")
    private String password;
}
