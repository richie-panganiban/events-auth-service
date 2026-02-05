package com.birthday.invitation.config;

import com.birthday.invitation.entity.Admin;
import com.birthday.invitation.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(AdminRepository adminRepository) {
        return args -> {
            // Create default admin if not exists
            if (!adminRepository.existsByEmail("admin@birthday-invitation.com")) {
                Admin admin = new Admin();
                admin.setEmail("admin@birthday-invitation.com");
                admin.setName("Admin User");
                admin.setIsActive(true);
                adminRepository.save(admin);

                log.info("Created default admin: admin@birthday-invitation.com");
                log.info("Request a magic link at POST /api/admin/auth/request with {\"email\": \"admin@birthday-invitation.com\"}");
            }
        };
    }
}
