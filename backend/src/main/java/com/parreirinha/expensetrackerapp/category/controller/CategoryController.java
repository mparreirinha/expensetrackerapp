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
import org.springframework.security.core.userdetails.UserDetails;
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
    @Operation(summary = "Get all categories", description = "Returns all categories for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping()
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getCategories(userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get category by ID", description = "Returns a category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied to this category", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(userDetails.getUsername(), id));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create category", description = "Creates a new category for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping()
    public ResponseEntity<Void> createCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody @Valid CategoryRequestDto dto) {
        categoryService.createCategory(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update category", description = "Updates a category by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied to update this category", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable @NotNull UUID id,
                                               @RequestBody @Valid CategoryRequestDto dto) {
        categoryService.updateCategory(id, userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete category", description = "Deletes a category by its ID and unsets it from transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied to delete this category", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable @NotNull UUID id) {
        categoryService.deleteCategory(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
