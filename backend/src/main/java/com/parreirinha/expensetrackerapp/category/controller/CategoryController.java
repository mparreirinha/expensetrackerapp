package com.parreirinha.expensetrackerapp.category.controller;

import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
    name = "Categories",
    description = "Endpoints for managing user-defined transaction categories"
)
@RequestMapping("/categories")
@Validated
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(categoryService.getCategories(jwt.getClaimAsString("preferred_username")));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@AuthenticationPrincipal Jwt jwt,
                                                                   @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(jwt.getClaimAsString("preferred_username"), id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<Void> createCategory(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody @Valid CategoryRequestDto dto) {
        categoryService.createCategory(jwt.getClaimAsString("preferred_username"), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable @NotNull UUID id,
                                               @RequestBody @Valid CategoryRequestDto dto) {
        categoryService.updateCategory(id, jwt.getClaimAsString("preferred_username"), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable @NotNull UUID id) {
        categoryService.deleteCategory(id, jwt.getClaimAsString("preferred_username"));
        return ResponseEntity.noContent().build();
    }

}
