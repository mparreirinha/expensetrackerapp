package com.parreirinha.expensetrackerapp.category.service;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.mapper.CategoryMapper;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.service.UserQueryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserQueryService userQueryService;

    public CategoryService(CategoryRepository categoryRepository, UserQueryService userQueryService) {
        this.categoryRepository = categoryRepository;
        this.userQueryService = userQueryService;
    }

    @Transactional
    public void createCategory(String username, CategoryRequestDto dto) {
        User user = userQueryService.getUserByUsername(username);
        Category category = CategoryMapper.INSTANCE.toCategory(dto);
        category.setUser(user);
        categoryRepository.save(category);
    }

    public List<CategoryResponseDto> getCategories(String username) {
        User user = userQueryService.getUserByUsername(username);
        List<Category> categories = categoryRepository.findByUser(user);
        return CategoryMapper.INSTANCE.toCategoryResponseDtoList(categories);
    }

    public CategoryResponseDto getCategory(String username, UUID id) {
        Category category = findCategoryById(id);
        if (!category.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have access to this category");
        return CategoryMapper.INSTANCE.toCategoryResponseDto(category);
    }

    @Transactional
    public void updateCategory(UUID id, String username, CategoryRequestDto dto) {
        Category category = findCategoryById(id);
        if (!category.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to update this category");
        category.setName(dto.name());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id, String username) {
        Category category = findCategoryById(id);
        if (!category.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to delete this category");
        categoryRepository.delete(category);
    }

    public Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public void deleteByUser(User user) {
        categoryRepository.deleteByUser(user);
    }

}
