package com.axislab.crmmedico.repository;

import com.axislab.crmmedico.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCpf(String cpf);
    boolean existsByCpf(String cpf);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Patient> findActiveByName(@Param("name") String name, Pageable pageable);

    Page<Patient> findByActiveTrue(Pageable pageable);
}
