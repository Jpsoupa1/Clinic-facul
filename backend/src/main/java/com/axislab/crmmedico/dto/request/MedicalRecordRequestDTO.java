package com.axislab.crmmedico.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDTO(
        @NotNull(message = "ID da consulta é obrigatório")
        Long appointmentId,

        @NotBlank(message = "Anamnese é obrigatória")
        String anamnesis,

        @NotBlank(message = "Diagnóstico é obrigatório")
        String diagnosis,

        @Size(max = 10)
        String cidCode,

        String prescription,

        String evolution
) {}
