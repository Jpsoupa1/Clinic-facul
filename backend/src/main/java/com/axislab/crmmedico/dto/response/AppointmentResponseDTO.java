package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.entity.Appointment;
import com.axislab.crmmedico.enums.AppointmentStatus;
import com.axislab.crmmedico.enums.PaymentType;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        Long doctorId,
        String doctorName,
        String doctorCrm,
        Long patientId,
        String patientName,
        String patientCpf,
        LocalDateTime dateTime,
        AppointmentStatus status,
        PaymentType paymentType,
        String notes,
        String cancellationReason,
        LocalDateTime createdAt
) {
    public static AppointmentResponseDTO from(Appointment a) {
        return new AppointmentResponseDTO(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getUser().getName(),
                a.getDoctor().getCrm(),
                a.getPatient().getId(),
                a.getPatient().getName(),
                a.getPatient().getCpf(),
                a.getDateTime(),
                a.getStatus(),
                a.getPaymentType(),
                a.getNotes(),
                a.getCancellationReason(),
                a.getCreatedAt()
        );
    }
}
