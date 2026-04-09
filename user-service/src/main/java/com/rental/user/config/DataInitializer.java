package com.rental.user.config;

import com.rental.user.model.Role;
import com.rental.user.model.User;
import com.rental.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("owner@demo.fr").isEmpty()) {
            User owner = new User();
            owner.setFirstName("Sophie");
            owner.setLastName("Martin");
            owner.setEmail("owner@demo.fr");
            owner.setPassword(passwordEncoder.encode("demo1234"));
            owner.setRole(Role.OWNER);
            owner.setPhone("+33 6 12 34 56 78");
            userRepository.save(owner);
        }

        if (userRepository.findByEmail("tenant@demo.fr").isEmpty()) {
            User tenant = new User();
            tenant.setFirstName("Lucas");
            tenant.setLastName("Bernard");
            tenant.setEmail("tenant@demo.fr");
            tenant.setPassword(passwordEncoder.encode("demo1234"));
            tenant.setRole(Role.TENANT);
            userRepository.save(tenant);
        }
    }
}
