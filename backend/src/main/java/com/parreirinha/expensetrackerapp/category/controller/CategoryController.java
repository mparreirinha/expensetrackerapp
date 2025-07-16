package com.parreirinha.expensetrackerapp.category.controller;

import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getCategories(userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(userDetails.getUsername(), id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    public ResponseEntity<Void> createCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody CategoryRequestDto dto) {
        categoryService.createCategory(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable UUID id,
                                               @RequestBody CategoryRequestDto dto) {
        categoryService.updateCategory(id, userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable UUID id) {
        categoryService.deleteCategory(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
