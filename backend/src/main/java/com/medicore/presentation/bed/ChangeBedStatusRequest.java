package com.medicore.presentation.bed;

import jakarta.validation.constraints.NotBlank;

public record ChangeBedStatusRequest(@NotBlank String status) {
}
