package com.expensetracker.converter;

import com.expensetracker.model.Category;
import com.expensetracker.web.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<CategoryDto, Category> {
  @Override
  public Category toModel(CategoryDto dto) {
    return null;
  }

  @Override
  public CategoryDto toDto(Category model) {
    return null;
  }
}
