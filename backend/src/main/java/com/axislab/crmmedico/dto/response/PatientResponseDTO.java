package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.entity.Patient;
import com.axislab.crmmedico.enums.BloodType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponseDTO(
        Long id,
        String name,
        String cpf,
        String email,
        LocalDate birthDate,
        String phone,
        String emergencyContact,
        BloodType bloodType,
        String allergies,
        String insuranceProvider,
        String insuranceNumber,
        Boolean active,
        LocalDateTime createdAt
) {
    public static PatientResponseDTO from(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getCpf(),
                patient.getEmail(),
                patient.getBirthDate(),
                patient.getPhone(),
                patient.getEmergencyContact(),
                patient.getBloodType(),
                patient.getAllergies(),
                patient.getInsuranceProvider(),
                patient.getInsuranceNumber(),
                patient.getActive(),
                patient.getCreatedAt()
        );
    }
}
