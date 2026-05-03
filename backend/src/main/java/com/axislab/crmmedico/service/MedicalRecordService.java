package com.axislab.crmmedico.service;

import com.axislab.crmmedico.dto.request.MedicalRecordRequestDTO;
import com.axislab.crmmedico.dto.response.MedicalRecordResponseDTO;
import com.axislab.crmmedico.entity.Appointment;
import com.axislab.crmmedico.entity.MedicalRecord;
import com.axislab.crmmedico.enums.AppointmentStatus;
import com.axislab.crmmedico.exception.BusinessException;
import com.axislab.crmmedico.exception.ConflictException;
import com.axislab.crmmedico.exception.ResourceNotFoundException;
import com.axislab.crmmedico.repository.AppointmentRepository;
import com.axislab.crmmedico.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public MedicalRecordResponseDTO create(MedicalRecordRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", dto.appointmentId()));

        if (appointment.getStatus() != AppointmentStatus.REALIZADA) {
            throw new BusinessException(
                    "Prontuário só pode ser criado para consultas com status REALIZADA. " +
                    "Status atual: " + appointment.getStatus()
            );
        }

        if (medicalRecordRepository.existsByAppointmentId(dto.appointmentId())) {
            throw new ConflictException("Já existe um prontuário para esta consulta (ID: " + dto.appointmentId() + ").");
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .anamnesis(dto.anamnesis())
                .diagnosis(dto.diagnosis())
                .cidCode(dto.cidCode())
                .prescription(dto.prescription())
                .evolution(dto.evolution())
                .build();

        return MedicalRecordResponseDTO.from(medicalRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO findById(Long id) {
        return MedicalRecordResponseDTO.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponseDTO> findByPatient(Long patientId, Pageable pageable) {
        return medicalRecordRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId, pageable)
                .map(MedicalRecordResponseDTO::from);
    }

    private MedicalRecord getOrThrow(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário", id));
    }
}
