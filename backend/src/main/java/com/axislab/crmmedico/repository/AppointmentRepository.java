package com.axislab.crmmedico.repository;

import com.axislab.crmmedico.entity.Appointment;
import com.axislab.crmmedico.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndDateTimeAndStatusNot(
            Long doctorId, LocalDateTime dateTime, AppointmentStatus status);

    @Query("""
            SELECT a FROM Appointment a
            WHERE (:doctorId IS NULL OR a.doctor.id = :doctorId)
              AND (:patientId IS NULL OR a.patient.id = :patientId)
              AND (:status IS NULL OR a.status = :status)
              AND (:from IS NULL OR a.dateTime >= :from)
              AND (:to IS NULL OR a.dateTime <= :to)
            """)
    Page<Appointment> findWithFilters(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("status") AppointmentStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
