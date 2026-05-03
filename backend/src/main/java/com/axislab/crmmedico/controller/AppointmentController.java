package com.axislab.crmmedico.controller;

import com.axislab.crmmedico.dto.request.AppointmentRequestDTO;
import com.axislab.crmmedico.dto.request.AppointmentStatusUpdateDTO;
import com.axislab.crmmedico.dto.response.AppointmentResponseDTO;
import com.axislab.crmmedico.enums.AppointmentStatus;
import com.axislab.crmmedico.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Consultas", description = "Gerenciamento de consultas e agendamentos")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova consulta")
    public AppointmentResponseDTO create(@Valid @RequestBody AppointmentRequestDTO dto) {
        return appointmentService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Listar consultas com filtros opcionais (médico, paciente, status, período)")
    public Page<AppointmentResponseDTO> list(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "dateTime") Pageable pageable) {
        return appointmentService.listAll(doctorId, patientId, status, from, to, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID")
    public AppointmentResponseDTO findById(@PathVariable Long id) {
        return appointmentService.findById(id);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar status da consulta")
    public AppointmentResponseDTO updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateDTO dto) {
        return appointmentService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancelar consulta")
    public void cancel(@PathVariable Long id,
                       @RequestParam(required = false) String reason) {
        appointmentService.cancel(id, reason);
    }
}
