package com.expensetracker.web;

import com.expensetracker.exception.ErrorCode;
import com.expensetracker.web.dto.CategoryDto;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends AbstractBaseControllerTest {

  @Test
  @SneakyThrows
  void getAllCategories() {
    int insertedCategoriesAmount = 3;

    for (int i = 0; i < insertedCategoriesAmount; i++) {
      insertCategory();
    }

    MvcResult mvcResult = mockMvc.perform(get("/categories"))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    List<CategoryDto> categoryDtos = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertTrue(categoryDtos.size() >= insertedCategoriesAmount);
  }

  @Test
  @SneakyThrows
  void getCategoryById() {
    CategoryDto insertedCategory = insertCategory();

    mockMvc.perform(get("/categories/{id}", insertedCategory.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedCategory.getId()))
        .andExpect(jsonPath("$.name").value(insertedCategory.getName()));
  }

  @Test
  @SneakyThrows
  void getNotExistingCategoryById_ReturnsErrorResponse() {
    CategoryDto insertedCategory = insertCategory();
    int notExistingCategoryId = insertedCategory.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Category with id='%s' not found", notExistingCategoryId);

    mockMvc.perform(get("/categories/{id}", notExistingCategoryId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void createCategory() {
    CategoryDto newCategory = categoryEntityProvider.prepareCategoryDto();

    MvcResult mvcResult = mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newCategory)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    CategoryDto insertedCategory = objectMapper.readValue(responseBody, CategoryDto.class);

    mockMvc.perform(get("/categories/{id}", insertedCategory.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedCategory.getId()))
        .andExpect(jsonPath("$.name").value(insertedCategory.getName()));
  }

  @Test
  @SneakyThrows
  void createCategoryWithExistingName_ReturnsErrorResponse() {
    CategoryDto newCategory = categoryEntityProvider.prepareCategoryDto();

    mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newCategory)))
        .andExpect(status().isCreated())
        .andReturn();

    String expectedErrorCode = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage = String.format(
        "Wasn't able to create new category. Category with name='%s' already exists",
        newCategory.getName()
    );

    mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newCategory)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void updateCategoryById() {
    CategoryDto insertedCategory = insertCategory();
    CategoryDto updatedCategory = categoryEntityProvider.prepareCategoryDto();

    mockMvc.perform(put("/categories/{id}", insertedCategory.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedCategory)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedCategory.getId()))
        .andExpect(jsonPath("$.name").value(updatedCategory.getName()));

    mockMvc.perform(get("/categories/{id}", insertedCategory.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedCategory.getId()))
        .andExpect(jsonPath("$.name").value(updatedCategory.getName()));
  }

  @Test
  @SneakyThrows
  void updateNotExistingCategoryById_ReturnsErrorResponse() {
    CategoryDto insertedCategory = insertCategory();
    CategoryDto updatedCategory = categoryEntityProvider.prepareCategoryDto();
    int notExistingCategoryId = insertedCategory.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Category with id='%s' not found", notExistingCategoryId);

    mockMvc.perform(put("/categories/{id}", notExistingCategoryId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedCategory)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void updateCategoryByIdWithExistingName_ReturnsErrorResponse() {
    CategoryDto insertedCategory1 = insertCategory();
    CategoryDto insertedCategory2 = insertCategory();

    CategoryDto updatedCategory1 = new CategoryDto(
        null,
        insertedCategory2.getName()
    );

    CategoryDto updatedCategory2 = new CategoryDto(
        null,
        insertedCategory1.getName()
    );

    String expectedErrorCode1 = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage1 = String.format(
        "Wasn't able to update existing category with id='%s'. Category with name='%s' already exists",
        insertedCategory1.getId(), updatedCategory1.getName()
    );

    mockMvc.perform(put("/categories/{id}", insertedCategory1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedCategory1)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode1))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage1));

    String expectedErrorCode2 = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage2 = String.format(
        "Wasn't able to update existing category with id='%s'. Category with name='%s' already exists",
        insertedCategory2.getId(), updatedCategory2.getName()
    );

    mockMvc.perform(put("/categories/{id}", insertedCategory2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedCategory2)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode2))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage2));
  }

  @Test
  @SneakyThrows
  void deleteCategoryById() {
    CategoryDto insertedCategory = insertCategory();

    mockMvc.perform(delete("/categories/{id}", insertedCategory.getId()))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void deleteNotExistingUserById_ReturnsErrorResponse() {
    CategoryDto insertedCategory = insertCategory();
    int notExistingCategoryId = insertedCategory.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Category with id='%s' not found", notExistingCategoryId);

    mockMvc.perform(delete("/categories/{id}", notExistingCategoryId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

}