package com.ms_auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthResponseDTO(
        @NotBlank String username,
        @NotBlank String password
) {}