package com.pavankotha.shortener.web.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlForm(
        @NotBlank(message = "Original Url is required")
        String originalUrl) {
}
