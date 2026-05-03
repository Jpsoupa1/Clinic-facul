package com.axislab.crmmedico.dto.request;

import com.axislab.crmmedico.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateDTO(
        @NotNull(message = "Status é obrigatório")
        AppointmentStatus status,

        String cancellationReason
) {}
