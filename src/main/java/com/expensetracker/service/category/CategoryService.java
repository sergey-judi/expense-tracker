package com.expensetracker.service.category;

import com.expensetracker.model.Category;

import java.util.List;

public interface CategoryService {

  List<Category> getAll();
  Category getById(Integer categoryId);
  Category createCategory(Category newCategory);
  Category updateCategoryById(Integer categoryId, Category updatedCategory);
  void deleteCategoryById(Integer categoryId);
  void assertCategoryExists(Integer categoryId);

}
