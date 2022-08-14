package com.groupfive.ewastemanagement.dto.request;

import lombok.Data;

@Data
public class PasswordResetDTO {
    String email;
    String newPassword;
}
