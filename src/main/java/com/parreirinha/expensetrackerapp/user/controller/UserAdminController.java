package com.parreirinha.expensetrackerapp.user.controller;

import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/admin/users")
@RestController
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserAdminResponseDto>> getUsers() {
        return ResponseEntity.ok(userAdminService.getUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponseDto> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userAdminService.getUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
