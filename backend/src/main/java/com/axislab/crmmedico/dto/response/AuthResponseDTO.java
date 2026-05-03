package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.enums.RoleType;

public record AuthResponseDTO(
        String token,
        String email,
        String name,
        RoleType role
) {}
