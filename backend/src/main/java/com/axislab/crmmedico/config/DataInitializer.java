package com.axislab.crmmedico.config;

import com.axislab.crmmedico.entity.User;
import com.axislab.crmmedico.enums.RoleType;
import com.axislab.crmmedico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@axislab.com").isEmpty()) {
            User admin = User.builder()
                    .name("Administrador")
                    .email("admin@axislab.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(RoleType.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Usuário admin criado: admin@axislab.com / Admin@123");
        }
    }
}
