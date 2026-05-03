package com.axislab.crmmedico.service;

import com.axislab.crmmedico.dto.request.AppointmentRequestDTO;
import com.axislab.crmmedico.dto.request.AppointmentStatusUpdateDTO;
import com.axislab.crmmedico.dto.response.AppointmentResponseDTO;
import com.axislab.crmmedico.entity.Appointment;
import com.axislab.crmmedico.entity.Doctor;
import com.axislab.crmmedico.entity.Patient;
import com.axislab.crmmedico.enums.AppointmentStatus;
import com.axislab.crmmedico.exception.BusinessException;
import com.axislab.crmmedico.exception.ConflictException;
import com.axislab.crmmedico.exception.ResourceNotFoundException;
import com.axislab.crmmedico.repository.AppointmentRepository;
import com.axislab.crmmedico.repository.DoctorRepository;
import com.axislab.crmmedico.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico", dto.doctorId()));

        if (!doctor.getActive()) {
            throw new BusinessException("Médico está inativo e não pode receber consultas.");
        }

        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", dto.patientId()));

        if (!patient.getActive()) {
            throw new BusinessException("Paciente está inativo.");
        }

        boolean conflict = appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctor.getId(), dto.dateTime(), AppointmentStatus.CANCELADA
        );
        if (conflict) {
            throw new ConflictException("Médico já possui consulta agendada neste horário: " + dto.dateTime());
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .dateTime(dto.dateTime())
                .paymentType(dto.paymentType())
                .notes(dto.notes())
                .status(AppointmentStatus.AGENDADA)
                .build();

        return AppointmentResponseDTO.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> listAll(Long doctorId, Long patientId,
                                                AppointmentStatus status,
                                                LocalDateTime from, LocalDateTime to,
                                                Pageable pageable) {
        return appointmentRepository
                .findWithFilters(doctorId, patientId, status, from, to, pageable)
                .map(AppointmentResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long id) {
        return AppointmentResponseDTO.from(getOrThrow(id));
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatusUpdateDTO dto) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new BusinessException("Não é possível alterar o status de uma consulta cancelada.");
        }
        if (appointment.getStatus() == AppointmentStatus.REALIZADA) {
            throw new BusinessException("Não é possível alterar o status de uma consulta já realizada.");
        }

        if (dto.status() == AppointmentStatus.CANCELADA && dto.cancellationReason() == null) {
            throw new BusinessException("Motivo de cancelamento é obrigatório.");
        }

        appointment.setStatus(dto.status());
        if (dto.cancellationReason() != null) {
            appointment.setCancellationReason(dto.cancellationReason());
        }

        return AppointmentResponseDTO.from(appointmentRepository.save(appointment));
    }

    @Transactional
    public void cancel(Long id, String reason) {
        Appointment appointment = getOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.REALIZADA) {
            throw new BusinessException("Não é possível cancelar uma consulta já realizada.");
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new BusinessException("Consulta já está cancelada.");
        }

        appointment.setStatus(AppointmentStatus.CANCELADA);
        appointment.setCancellationReason(reason != null ? reason : "Cancelado pelo usuário.");
        appointmentRepository.save(appointment);
    }

    private Appointment getOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
    }
}
