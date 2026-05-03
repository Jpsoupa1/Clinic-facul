package com.axislab.crmmedico.controller;

import com.axislab.crmmedico.dto.request.PatientRequestDTO;
import com.axislab.crmmedico.dto.response.PatientResponseDTO;
import com.axislab.crmmedico.service.PatientService;
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
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Pacientes", description = "Gerenciamento de pacientes")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Cadastrar novo paciente")
    public PatientResponseDTO create(@Valid @RequestBody PatientRequestDTO dto) {
        return patientService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Listar pacientes ativos com filtro opcional por nome")
    public Page<PatientResponseDTO> list(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return patientService.listAll(name, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public PatientResponseDTO findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Atualizar dados do paciente")
    public PatientResponseDTO update(@PathVariable Long id, @Valid @RequestBody PatientRequestDTO dto) {
        return patientService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Desativar paciente — soft delete (apenas ADMIN)")
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
