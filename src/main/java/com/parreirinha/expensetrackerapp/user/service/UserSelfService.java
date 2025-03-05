package com.parreirinha.expensetrackerapp.user.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parreirinha.expensetrackerapp.user.dto.ChangePasswordDto;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;

@Service
public class UserSelfService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserSelfService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);
    }
    
}
