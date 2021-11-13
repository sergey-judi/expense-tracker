package com.expensetracker.converter;

import com.expensetracker.model.Category;
import com.expensetracker.web.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<CategoryDto, Category> {
  @Override
  public Category toModel(CategoryDto dto) {
    return new Category(
        dto.getId(),
        dto.getName()
    );
  }

  @Override
  public CategoryDto toDto(Category model) {
    return new CategoryDto(
        model.getId(),
        model.getName()
    );
  }
}
