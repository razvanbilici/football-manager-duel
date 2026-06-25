package com.football.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePlayerRequest {
    @NotBlank(message = "Numele jucatorului este obligatoriu")
    public String name;

    public String nickname;

    @NotBlank(message = "Pozitia este obligatorie")
    public String position;

    @NotNull
    public Long playerTeamId;

    @NotNull(message = "Pretul este obligatoriu")
    @Min(value = 0, message = "Pretul nu poate fi negativ")
    public BigDecimal price;

    @Min(value = 15, message = "Varsta minima este 15")
    @Max(value = 50, message = "Varsta maxima este 50")
    public Integer age;

    @Min(value = 150, message = "Inaltimea minima este 150cm")
    @Max(value = 220, message = "Inaltimea maxima este 220cm")
    public Integer heightCm;

    public String nationality;
    public String preferredFoot;

    @Min(value = 1, message = "Numarul de tricou minim este 1")
    @Max(value = 99, message = "Numarul de tricou maxim este 99")
    public Integer shirtNumber;

    public Boolean available;
}
