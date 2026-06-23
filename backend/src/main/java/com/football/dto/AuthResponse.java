package com.football.dto;
import lombok.Data;
@Data public class AuthResponse {
    public String token;
    public Long userId;
    public String name;
    public String email;
}
