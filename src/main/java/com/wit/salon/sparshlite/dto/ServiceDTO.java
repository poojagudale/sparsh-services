package com.wit.salon.sparshlite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {

    private Long serviceId;

    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name must not exceed 100 characters")
    private String serviceName;

    @NotNull(message = "isActive status is required")
    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
