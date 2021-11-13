package com.expensetracker.web;

import com.expensetracker.converter.CategoryConverter;
import com.expensetracker.model.Category;
import com.expensetracker.service.category.CategoryService;
import com.expensetracker.web.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryConverter categoryConverter;

  @GetMapping
  public List<CategoryDto> getAllCategories() {
    return categoryService.getAll().stream()
        .map(categoryConverter::toDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public CategoryDto getCategory(@PathVariable Integer id) {
    Category retrievedCategory = categoryService.getById(id);
    return categoryConverter.toDto(retrievedCategory);
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public CategoryDto createCategory(@Validated @RequestBody CategoryDto newCategoryDto) {
    Category givenCategory = categoryConverter.toModel(newCategoryDto);
    Category createdCategory = categoryService.createCategory(givenCategory);
    return categoryConverter.toDto(createdCategory);
  }

  @PutMapping("/{id}")
  public CategoryDto updateCategory(@PathVariable Integer id,
                                    @Validated @RequestBody CategoryDto updatedCategoryDto) {
    Category givenCategory = categoryConverter.toModel(updatedCategoryDto);
    Category updatedCategory = categoryService.updateCategoryById(id, givenCategory);
    return categoryConverter.toDto(updatedCategory);
  }

  @DeleteMapping("/{id}")
  public void deleteCategory(@PathVariable Integer id) {
    categoryService.deleteCategoryById(id);
  }

}
