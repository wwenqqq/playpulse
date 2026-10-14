package com.example.taskmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GameCreateDTO {
    @NotBlank @Size(max = 120) private String title;
    @NotBlank @Size(max = 90) private String studio;
    @NotNull @Min(1950) @Max(2100) private Integer releaseYear;
    @NotBlank @Size(max = 120) private String genres;
    @NotBlank @Pattern(regexp = "lime|amber|violet|cyan|rose|ice") private String coverTone;
    @NotBlank @Size(max = 3000) private String summary;
}
