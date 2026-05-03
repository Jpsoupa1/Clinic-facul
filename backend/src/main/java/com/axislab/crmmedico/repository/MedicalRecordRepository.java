package com.axislab.crmmedico.repository;

import com.axislab.crmmedico.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Page<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId, Pageable pageable);
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
    boolean existsByAppointmentId(Long appointmentId);
}
