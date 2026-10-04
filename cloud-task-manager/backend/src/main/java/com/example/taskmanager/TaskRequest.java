package com.example.taskmanager;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "title is required") @Size(max = 255) String title,
        @Size(max = 2000) String description,
        TaskStatus status) {
}
