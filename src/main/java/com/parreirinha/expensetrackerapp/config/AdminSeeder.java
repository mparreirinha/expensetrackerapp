package com.parreirinha.expensetrackerapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.parreirinha.expensetrackerapp.user.domain.Role;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private String username = "admin";

    private String email = "admin@email.com";

    private String password = "admin";

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }

}
