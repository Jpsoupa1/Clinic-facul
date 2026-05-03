package com.axislab.crmmedico.dto.request;

import com.axislab.crmmedico.enums.PaymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        @NotNull(message = "ID do médico é obrigatório")
        Long doctorId,

        @NotNull(message = "ID do paciente é obrigatório")
        Long patientId,

        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "A consulta deve ser agendada para uma data futura")
        LocalDateTime dateTime,

        @NotNull(message = "Tipo de pagamento é obrigatório")
        PaymentType paymentType,

        String notes
) {}
