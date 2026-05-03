package com.axislab.crmmedico.controller;

import com.axislab.crmmedico.dto.request.MedicalRecordRequestDTO;
import com.axislab.crmmedico.dto.response.MedicalRecordResponseDTO;
import com.axislab.crmmedico.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Prontuários", description = "Gerenciamento de prontuários eletrônicos")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDICO')")
    @Operation(summary = "Criar prontuário eletrônico (consulta deve estar com status REALIZADA)")
    public MedicalRecordResponseDTO create(@Valid @RequestBody MedicalRecordRequestDTO dto) {
        return medicalRecordService.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDICO')")
    @Operation(summary = "Buscar prontuário por ID")
    public MedicalRecordResponseDTO findById(@PathVariable Long id) {
        return medicalRecordService.findById(id);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDICO')")
    @Operation(summary = "Listar histórico de prontuários de um paciente")
    public Page<MedicalRecordResponseDTO> findByPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return medicalRecordService.findByPatient(patientId, pageable);
    }
}
