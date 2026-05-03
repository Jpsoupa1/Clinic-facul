package com.axislab.crmmedico.service;

import com.axislab.crmmedico.dto.request.AuthRequestDTO;
import com.axislab.crmmedico.dto.request.RegisterRequestDTO;
import com.axislab.crmmedico.dto.response.AuthResponseDTO;
import com.axislab.crmmedico.dto.response.UserResponseDTO;
import com.axislab.crmmedico.entity.User;
import com.axislab.crmmedico.exception.BusinessException;
import com.axislab.crmmedico.repository.UserRepository;
import com.axislab.crmmedico.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(AuthRequestDTO dto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.email());
        String token = jwtProvider.generateToken(userDetails);

        User user = userRepository.findByEmail(dto.email()).orElseThrow();
        return new AuthResponseDTO(token, user.getEmail(), user.getName(), user.getRole());
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("E-mail já cadastrado: " + dto.email());
        }

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .active(true)
                .build();

        return UserResponseDTO.from(userRepository.save(user));
    }
}
