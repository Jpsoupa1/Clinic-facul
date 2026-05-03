package com.axislab.crmmedico.dto.request;

import com.axislab.crmmedico.enums.BloodType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150)
        String name,

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 14, message = "CPF deve ter entre 11 e 14 caracteres")
        String cpf,

        @Email(message = "Formato de e-mail inválido")
        @Size(max = 255)
        String email,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate birthDate,

        @Size(max = 20)
        String phone,

        @Size(max = 200)
        String emergencyContact,

        BloodType bloodType,

        String allergies,

        @Size(max = 100)
        String insuranceProvider,

        @Size(max = 50)
        String insuranceNumber
) {}
