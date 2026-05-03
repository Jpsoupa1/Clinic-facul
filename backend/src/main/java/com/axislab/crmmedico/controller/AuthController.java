package com.axislab.crmmedico.controller;

import com.axislab.crmmedico.dto.request.AuthRequestDTO;
import com.axislab.crmmedico.dto.request.RegisterRequestDTO;
import com.axislab.crmmedico.dto.response.AuthResponseDTO;
import com.axislab.crmmedico.dto.response.UserResponseDTO;
import com.axislab.crmmedico.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e cadastro de usuários do sistema")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Registrar novo usuário do sistema (apenas ADMIN)")
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
        return authService.register(dto);
    }
}
