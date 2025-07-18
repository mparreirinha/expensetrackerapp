package com.parreirinha.expensetrackerapp.category.mapper;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toCategory(CategoryRequestDto dto);

    CategoryResponseDto toCategoryResponseDto(Category category);

    List<CategoryResponseDto> toCategoryResponseDtoList(List<Category> categories);

}
