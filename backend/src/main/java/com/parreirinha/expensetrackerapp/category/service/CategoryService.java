package com.parreirinha.expensetrackerapp.category.service;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import com.parreirinha.expensetrackerapp.category.mapper.CategoryMapper;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,
                             UserRepository userRepository,
                             TransactionRepository transactionRepository,
                             CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public void createCategory(String username, CategoryRequestDto dto) {
        User user = getUserByUsername(username);
        Category category = categoryMapper.toCategory(dto);
        category.setUser(user);
        categoryRepository.save(category);
    }

    public List<CategoryResponseDto> getCategories(String username) {
        User user = getUserByUsername(username);
        List<Category> categories = categoryRepository.findByUser(user);
        return categoryMapper.toCategoryResponseDtoList(categories);
    }

    public CategoryResponseDto getCategory(String username, UUID id) {
        Category category = findCategoryById(id);
        if (!category.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have access to this category");
        return categoryMapper.toCategoryResponseDto(category);
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
        transactionRepository.unsetCategoryFromTransactions(category);
        categoryRepository.delete(category);
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
