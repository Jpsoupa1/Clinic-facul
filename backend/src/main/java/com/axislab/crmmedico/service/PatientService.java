package com.axislab.crmmedico.service;

import com.axislab.crmmedico.dto.request.PatientRequestDTO;
import com.axislab.crmmedico.dto.response.PatientResponseDTO;
import com.axislab.crmmedico.entity.Patient;
import com.axislab.crmmedico.exception.BusinessException;
import com.axislab.crmmedico.exception.ResourceNotFoundException;
import com.axislab.crmmedico.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto) {
        if (patientRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.cpf());
        }

        Patient patient = Patient.builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .email(dto.email())
                .birthDate(dto.birthDate())
                .phone(dto.phone())
                .emergencyContact(dto.emergencyContact())
                .bloodType(dto.bloodType())
                .allergies(dto.allergies())
                .insuranceProvider(dto.insuranceProvider())
                .insuranceNumber(dto.insuranceNumber())
                .active(true)
                .build();

        return PatientResponseDTO.from(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> listAll(String name, Pageable pageable) {
        if (name != null && !name.isBlank()) {
            return patientRepository.findActiveByName(name, pageable).map(PatientResponseDTO::from);
        }
        return patientRepository.findByActiveTrue(pageable).map(PatientResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findById(Long id) {
        return PatientResponseDTO.from(getPatientOrThrow(id));
    }

    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
        Patient patient = getPatientOrThrow(id);

        if (!patient.getCpf().equals(dto.cpf()) && patientRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado: " + dto.cpf());
        }

        patient.setName(dto.name());
        patient.setCpf(dto.cpf());
        patient.setEmail(dto.email());
        patient.setBirthDate(dto.birthDate());
        patient.setPhone(dto.phone());
        patient.setEmergencyContact(dto.emergencyContact());
        patient.setBloodType(dto.bloodType());
        patient.setAllergies(dto.allergies());
        patient.setInsuranceProvider(dto.insuranceProvider());
        patient.setInsuranceNumber(dto.insuranceNumber());

        return PatientResponseDTO.from(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        Patient patient = getPatientOrThrow(id);
        patient.setActive(false);
        patientRepository.save(patient);
    }

    private Patient getPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }
}
