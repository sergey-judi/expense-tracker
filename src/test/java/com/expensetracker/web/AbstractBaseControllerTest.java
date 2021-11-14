package com.expensetracker.web;

import com.expensetracker.web.dto.CategoryDto;
import com.expensetracker.web.dto.TransactionDto;
import com.expensetracker.web.dto.UserDto;
import com.expensetracker.web.util.CategoryEntityProvider;
import com.expensetracker.web.util.TransactionEntityProvider;
import com.expensetracker.web.util.UserEntityProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractBaseControllerTest {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  UserEntityProvider userEntityProvider;

  @Autowired
  CategoryEntityProvider categoryEntityProvider;

  @Autowired
  TransactionEntityProvider transactionEntityProvider;

  @Autowired
  MockMvc mockMvc;

  @SneakyThrows
  public UserDto insertUser() {
    UserDto userDto = userEntityProvider.prepareUserDto();

    MvcResult mvcResult = mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(userDto)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, UserDto.class);
  }

  @SneakyThrows
  public CategoryDto insertCategory() {
    CategoryDto categoryDto = categoryEntityProvider.prepareCategoryDto();

    MvcResult mvcResult = mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(categoryDto)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, CategoryDto.class);
  }

  @SneakyThrows
  public TransactionDto insertTransaction(String type) {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    TransactionDto transactionDto = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, type);

    return insertTransactionGeneric(transactionDto);
  }

  @SneakyThrows
  public TransactionDto insertTransactionForDefiniteUser(UserDto userDto, String type) {
    CategoryDto categoryDto = insertCategory();
    TransactionDto transactionDto = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, type);

    return insertTransactionGeneric(transactionDto);
  }

  @SneakyThrows
  public TransactionDto insertTransactionForDefiniteUserAndCategory(UserDto userDto, CategoryDto categoryDto, String type) {
    TransactionDto transactionDto = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, type);

    return insertTransactionGeneric(transactionDto);
  }

  @SneakyThrows
  private TransactionDto insertTransactionGeneric(TransactionDto transactionDto) {
    MvcResult mvcResult = mockMvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(transactionDto)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, TransactionDto.class);
  }

  @SneakyThrows
  public String serialize(Object object) {
    return objectMapper.writeValueAsString(object);
  }

}
