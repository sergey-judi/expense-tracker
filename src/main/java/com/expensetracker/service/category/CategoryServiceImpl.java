package com.expensetracker.service.category;

import com.expensetracker.exception.EntityAlreadyExistsException;
import com.expensetracker.exception.EntityNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<Category> getAll() {
    return categoryRepository.findAll();
  }

  @Override
  public Category getById(Integer categoryId) {
    assertCategoryExists(categoryId);
    return categoryRepository.getById(categoryId);
  }

  @Override
  public Category createCategory(Category newCategory) {
    if (categoryRepository.existsByName(newCategory.getName())) {
      String message = format(
          "Wasn't able to create new category. Category with name='%s' already exists",
          newCategory.getName()
      );
      throw new EntityAlreadyExistsException(message);
    }
    return categoryRepository.save(newCategory);
  }

  @Override
  public Category updateCategoryById(Integer categoryId, Category updatedCategory) {
    assertCategoryExists(categoryId);

    if (categoryRepository.existsByName(updatedCategory.getName())) {
      String message = format(
          "Wasn't able to update existing category with id='%s'. Category with name='%s' already exists",
          categoryId, updatedCategory.getName()
      );
      throw new EntityAlreadyExistsException(message);
    }

    updatedCategory.setId(categoryId);
    return categoryRepository.save(updatedCategory);
  }

  @Override
  public void deleteCategoryById(Integer categoryId) {
    assertCategoryExists(categoryId);
    categoryRepository.deleteById(categoryId);
  }

  @Override
  public void assertCategoryExists(Integer categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new EntityNotFoundException(format("Category with id='%s' not found", categoryId));
    }
  }
}
