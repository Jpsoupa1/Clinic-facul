package com.axislab.crmmedico.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoctorRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150)
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100)
        String password,

        @NotBlank(message = "CRM é obrigatório")
        @Size(max = 20)
        String crm,

        @NotBlank(message = "Especialidade é obrigatória")
        @Size(max = 100)
        String specialty,

        @Size(max = 20)
        String phone
) {}
