package com.medicore.presentation.bed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BedRequest(
    @NotBlank String number,
    @NotNull Integer floor,
    @NotBlank String ward,
    @NotBlank String type
) {
}
