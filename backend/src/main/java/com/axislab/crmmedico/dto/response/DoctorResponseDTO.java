package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.entity.Doctor;

public record DoctorResponseDTO(
        Long id,
        String name,
        String email,
        String crm,
        String specialty,
        String phone,
        Boolean active
) {
    public static DoctorResponseDTO from(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getCrm(),
                doctor.getSpecialty(),
                doctor.getPhone(),
                doctor.getActive()
        );
    }
}
