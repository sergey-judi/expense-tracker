package com.expensetracker.web.util;

import com.expensetracker.converter.CategoryConverter;
import com.expensetracker.model.Category;
import com.expensetracker.web.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryEntityProvider {

  private final CategoryConverter categoryConverter;

  public CategoryDto prepareCategoryDto() {
    long currentTime = System.currentTimeMillis();

    Integer id = null;
    String name = String.format("category-%s-name", currentTime);

    return new CategoryDto(id, name);
  }

  public Category prepareCategoryModel() {
    return categoryConverter.toModel(prepareCategoryDto());
  }

}
