package com.expensetracker.web;

import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.CategoryDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HttpServiceTest extends AbstractBaseControllerTest {

  String CATEGORY_URI = "http://localhost:8181/categories";

  @Autowired
  HttpService<Object> httpService;

  @Test
  @SneakyThrows
  void getCategoryById() {
    CategoryDto insertedCategory = insertCategory();

    CategoryDto queriedCategory = httpService.get(CATEGORY_URI + "/" + insertedCategory.getId(), CategoryDto.class);

    assertEquals(insertedCategory, queriedCategory);
  }

  @Test
  @SneakyThrows
  void createCategory() {
    CategoryDto insertedCategory = insertCategory();
    CategoryDto categoryDto = categoryEntityProvider.prepareCategoryDto();

    httpService.post(categoryDto, CATEGORY_URI);

    CategoryDto queriedCategory = httpService.get(CATEGORY_URI + "/" + (insertedCategory.getId() + 1), CategoryDto.class);

    assertEquals(categoryDto.getName(), queriedCategory.getName());
  }

  @Test
  @SneakyThrows
  void updateCategory() {
    CategoryDto insertedCategory = insertCategory();

    CategoryDto updatedCategory = categoryEntityProvider.prepareCategoryDto();

    httpService.put(updatedCategory, CATEGORY_URI + "/" + insertedCategory.getId());

    CategoryDto queriedCategory = httpService.get(CATEGORY_URI + "/" + insertedCategory.getId(), CategoryDto.class);

    assertEquals(updatedCategory.getName(), queriedCategory.getName());
  }

  @Test
  @SneakyThrows
  void deleteCategory() {
    CategoryDto insertedCategory = insertCategory();

    httpService.delete(CATEGORY_URI + "/" + insertedCategory.getId());
  }

}
