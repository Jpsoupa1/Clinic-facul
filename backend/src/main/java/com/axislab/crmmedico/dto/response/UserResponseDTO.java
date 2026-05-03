package com.axislab.crmmedico.dto.response;

import com.axislab.crmmedico.entity.User;
import com.axislab.crmmedico.enums.RoleType;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        RoleType role,
        Boolean active,
        LocalDateTime createdAt
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }
}
