package com.axislab.crmmedico.repository;

import com.axislab.crmmedico.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByCrm(String crm);
    boolean existsByUserEmail(String email);
    Optional<Doctor> findByUserEmail(String email);

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE d.active = true AND " +
           "(:specialty IS NULL OR LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%')))")
    Page<Doctor> findActiveBySpecialty(@Param("specialty") String specialty, Pageable pageable);

    Page<Doctor> findByActiveTrue(Pageable pageable);
    List<Doctor> findBySpecialtyContainingIgnoreCase(String specialty);
}
