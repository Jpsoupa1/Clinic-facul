package com.axislab.crmmedico.controller;

import com.axislab.crmmedico.dto.request.DoctorRequestDTO;
import com.axislab.crmmedico.dto.response.DoctorResponseDTO;
import com.axislab.crmmedico.service.DoctorService;
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
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Médicos", description = "Gerenciamento de médicos")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Cadastrar novo médico (apenas ADMIN)")
    public DoctorResponseDTO create(@Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Listar médicos ativos com filtro opcional por especialidade")
    public Page<DoctorResponseDTO> list(
            @RequestParam(required = false) String specialty,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return doctorService.listAll(specialty, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID")
    public DoctorResponseDTO findById(@PathVariable Long id) {
        return doctorService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Atualizar dados do médico (apenas ADMIN)")
    public DoctorResponseDTO update(@PathVariable Long id, @Valid @RequestBody DoctorRequestDTO dto) {
        return doctorService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Desativar médico — soft delete (apenas ADMIN)")
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }
}
