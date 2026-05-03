package com.axislab.crmmedico.service;

import com.axislab.crmmedico.dto.request.DoctorRequestDTO;
import com.axislab.crmmedico.dto.response.DoctorResponseDTO;
import com.axislab.crmmedico.entity.Doctor;
import com.axislab.crmmedico.entity.User;
import com.axislab.crmmedico.enums.RoleType;
import com.axislab.crmmedico.exception.BusinessException;
import com.axislab.crmmedico.exception.ResourceNotFoundException;
import com.axislab.crmmedico.repository.DoctorRepository;
import com.axislab.crmmedico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DoctorResponseDTO create(DoctorRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.email());
        }
        if (doctorRepository.existsByCrm(dto.crm())) {
            throw new BusinessException("CRM já cadastrado: " + dto.crm());
        }

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(RoleType.MEDICO)
                .active(true)
                .build();

        Doctor doctor = Doctor.builder()
                .user(user)
                .crm(dto.crm())
                .specialty(dto.specialty())
                .phone(dto.phone())
                .active(true)
                .build();

        return DoctorResponseDTO.from(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponseDTO> listAll(String specialty, Pageable pageable) {
        if (specialty != null && !specialty.isBlank()) {
            return doctorRepository.findActiveBySpecialty(specialty, pageable)
                    .map(DoctorResponseDTO::from);
        }
        return doctorRepository.findByActiveTrue(pageable).map(DoctorResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public DoctorResponseDTO findById(Long id) {
        return DoctorResponseDTO.from(getDoctorOrThrow(id));
    }

    @Transactional
    public DoctorResponseDTO update(Long id, DoctorRequestDTO dto) {
        Doctor doctor = getDoctorOrThrow(id);

        String currentEmail = doctor.getUser().getEmail();
        if (!currentEmail.equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.email());
        }

        String currentCrm = doctor.getCrm();
        if (!currentCrm.equals(dto.crm()) && doctorRepository.existsByCrm(dto.crm())) {
            throw new BusinessException("CRM já cadastrado: " + dto.crm());
        }

        User user = doctor.getUser();
        user.setName(dto.name());
        user.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        doctor.setCrm(dto.crm());
        doctor.setSpecialty(dto.specialty());
        doctor.setPhone(dto.phone());

        return DoctorResponseDTO.from(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = getDoctorOrThrow(id);
        doctor.setActive(false);
        doctor.getUser().setActive(false);
        doctorRepository.save(doctor);
    }

    private Doctor getDoctorOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico", id));
    }
}
