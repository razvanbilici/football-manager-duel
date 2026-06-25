package com.football.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    public String currentPassword;
    public String newPassword;
}
