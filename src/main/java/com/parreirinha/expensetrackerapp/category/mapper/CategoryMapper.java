package com.parreirinha.expensetrackerapp.category.mapper;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.dto.CategoryRequestDto;
import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toCategory(CategoryRequestDto dto);

    CategoryResponseDto toCategoryResponseDto(Category category);

    List<CategoryResponseDto> toCategoryResponseDtoList(List<Category> categories);

}
