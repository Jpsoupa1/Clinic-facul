package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.entity.MedicalRecord;

import java.time.LocalDateTime;

public record MedicalRecordResponseDTO(
        Long id,
        Long appointmentId,
        Long doctorId,
        String doctorName,
        Long patientId,
        String patientName,
        String anamnesis,
        String diagnosis,
        String cidCode,
        String prescription,
        String evolution,
        LocalDateTime createdAt
) {
    public static MedicalRecordResponseDTO from(MedicalRecord mr) {
        return new MedicalRecordResponseDTO(
                mr.getId(),
                mr.getAppointment().getId(),
                mr.getDoctor().getId(),
                mr.getDoctor().getUser().getName(),
                mr.getPatient().getId(),
                mr.getPatient().getName(),
                mr.getAnamnesis(),
                mr.getDiagnosis(),
                mr.getCidCode(),
                mr.getPrescription(),
                mr.getEvolution(),
                mr.getCreatedAt()
        );
    }
}
