package com.expensetracker.web;

import com.expensetracker.exception.ErrorCode;
import com.expensetracker.exception.UnsupportedTransactionTypeException;
import com.expensetracker.web.dto.CategoryDto;
import com.expensetracker.web.dto.TransactionDto;
import com.expensetracker.web.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest extends AbstractBaseControllerTest {

  private static final Double ALLOWED_BALANCE_DEVIATION = 0.0001;

  private void assertBalanceEquals(double expected, double actual) {
    assertTrue(Math.abs(expected - actual) <= ALLOWED_BALANCE_DEVIATION);
  }

  @Test
  @SneakyThrows
  void getAllTransactions() {
    int insertedTransactionsAmount = 3;

    for (int i = 0; i < insertedTransactionsAmount; i++) {
      insertTransaction("debit");
    }

    MvcResult mvcResult = mockMvc.perform(get("/transactions"))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    List<TransactionDto> transactionDtos = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertTrue(transactionDtos.size() >= insertedTransactionsAmount);
  }

  @Test
  @SneakyThrows
  void getAllTransactionsByUserId() {
    UserDto userDto = insertUser();
    String transactionType = "debit";

    int insertedTransactionsAmount = 3;
    Double transactedAmount = 0.0;

    for (int i = 0; i < insertedTransactionsAmount; i++) {
      TransactionDto insertedTransaction = insertTransactionForDefiniteUser(userDto, transactionType);
      transactedAmount += insertedTransaction.getAmount();
    }

    MvcResult mvcResult = mockMvc.perform(get("/transactions/user/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    List<TransactionDto> transactionDtos = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertTrue(transactionDtos.size() >= insertedTransactionsAmount);

    Double expectedUserBalance = transactionType.equals("debit")
        ? userDto.getBalance() - transactedAmount
        : userDto.getBalance() + transactedAmount;

    mvcResult = mockMvc.perform(get("/users/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertBalanceEquals(insertedUser.getBalance(), expectedUserBalance);
  }

  @Test
  @SneakyThrows
  void getAllTransactionsByNotExistingUserId_ReturnsErrorResponse() {
    UserDto insertedUser = insertUser();
    int notExistingUserId = insertedUser.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("User with id='%s' not found", notExistingUserId);

    mockMvc.perform(get("/transactions/user/{id}", notExistingUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void getTransactionById() {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    String transactionType = "debit";
    TransactionDto insertedTransaction = insertTransactionForDefiniteUserAndCategory(userDto, categoryDto, transactionType);

    String expectedTransactionTime = serialize(insertedTransaction.getTime()).replace("\"", "");

    mockMvc.perform(get("/transactions/{id}", insertedTransaction.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedTransaction.getId()))
        .andExpect(jsonPath("$.userId").value(insertedTransaction.getUserId()))
        .andExpect(jsonPath("$.categoryId").value(insertedTransaction.getCategoryId()))
        .andExpect(jsonPath("$.type").value(insertedTransaction.getType().getCanonicalType()))
        .andExpect(jsonPath("$.amount").value(insertedTransaction.getAmount()))
        .andExpect(jsonPath("$.time").value(expectedTransactionTime));

    Double expectedUserBalance = transactionType.equals("debit")
        ? userDto.getBalance() - insertedTransaction.getAmount()
        : userDto.getBalance() + insertedTransaction.getAmount();

    MvcResult mvcResult = mockMvc.perform(get("/users/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertBalanceEquals(insertedUser.getBalance(), expectedUserBalance);
  }

  @Test
  @SneakyThrows
  void getNotExistingTransactionById_ReturnsErrorResponse() {
    TransactionDto insertedTransaction = insertTransaction("debit");
    int notExistingTransactionId = insertedTransaction.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Transaction with id='%s' not found", notExistingTransactionId);

    mockMvc.perform(get("/transactions/{id}", notExistingTransactionId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void createTransaction() {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    String transactionType = "debit";
    TransactionDto transactionDto = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, transactionType);

    MvcResult mvcResult = mockMvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(transactionDto)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    TransactionDto insertedTransaction = objectMapper.readValue(responseBody, TransactionDto.class);

    String expectedTransactionTime = serialize(insertedTransaction.getTime()).replace("\"", "");

    mockMvc.perform(get("/transactions/{id}", insertedTransaction.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedTransaction.getId()))
        .andExpect(jsonPath("$.userId").value(insertedTransaction.getUserId()))
        .andExpect(jsonPath("$.categoryId").value(insertedTransaction.getCategoryId()))
        .andExpect(jsonPath("$.type").value(insertedTransaction.getType().getCanonicalType()))
        .andExpect(jsonPath("$.amount").value(insertedTransaction.getAmount()))
        .andExpect(jsonPath("$.time").value(expectedTransactionTime));

    Double expectedUserBalance = transactionType.equals("debit")
        ? userDto.getBalance() - insertedTransaction.getAmount()
        : userDto.getBalance() + insertedTransaction.getAmount();

    mvcResult = mockMvc.perform(get("/users/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertBalanceEquals(insertedUser.getBalance(), expectedUserBalance);
  }

  @Test
  @SneakyThrows
  void createDebitAndThenCreditTransactionForDefiniteUser() {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    TransactionDto debitTransactionDto = insertTransactionForDefiniteUserAndCategory(userDto, categoryDto, "debit");
    TransactionDto creditTransactionDto = insertTransactionForDefiniteUserAndCategory(userDto, categoryDto, "credit");

    Double expectedUserBalance = userDto.getBalance() - debitTransactionDto.getAmount() + creditTransactionDto.getAmount();

    MvcResult mvcResult = mockMvc.perform(get("/users/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertBalanceEquals(insertedUser.getBalance(), expectedUserBalance);
  }

  @Test
  @SneakyThrows
  void updateTransactionById() {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    String transactionType = "debit";
    TransactionDto insertedTransaction = insertTransactionForDefiniteUserAndCategory(userDto, categoryDto, transactionType);
    TransactionDto updatedTransaction = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, transactionType);

    String expectedTransactionTime = serialize(updatedTransaction.getTime()).replace("\"", "");

    mockMvc.perform(put("/transactions/{id}", insertedTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedTransaction)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedTransaction.getId()))
        .andExpect(jsonPath("$.userId").value(updatedTransaction.getUserId()))
        .andExpect(jsonPath("$.categoryId").value(updatedTransaction.getCategoryId()))
        .andExpect(jsonPath("$.type").value(updatedTransaction.getType().getCanonicalType()))
        .andExpect(jsonPath("$.amount").value(updatedTransaction.getAmount()))
        .andExpect(jsonPath("$.time").value(expectedTransactionTime));

    Double expectedUserBalance = transactionType.equals("debit")
        ? userDto.getBalance() - updatedTransaction.getAmount()
        : userDto.getBalance() + updatedTransaction.getAmount();

    MvcResult mvcResult = mockMvc.perform(get("/users/{id}", userDto.getId()))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertBalanceEquals(insertedUser.getBalance(), expectedUserBalance);
  }

  @Test
  @SneakyThrows
  void updateNotExistingTransactionById_ReturnsErrorResponse() {
    UserDto userDto = insertUser();
    CategoryDto categoryDto = insertCategory();
    String transactionType = "debit";
    TransactionDto insertedTransaction = insertTransactionForDefiniteUserAndCategory(userDto, categoryDto, transactionType);
    TransactionDto updatedTransaction = transactionEntityProvider.prepareTransactionDto(userDto, categoryDto, transactionType);
    int notExistingTransactionId = insertedTransaction.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Transaction with id='%s' not found", notExistingTransactionId);

    mockMvc.perform(put("/transactions/{id}", notExistingTransactionId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedTransaction)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void deleteTransactionById() {
    TransactionDto insertedTransaction = insertTransaction("debit");

    mockMvc.perform(delete("/transactions/{id}", insertedTransaction.getId()))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void deleteNotExistingTransactionById_ReturnsErrorResponse() {
    TransactionDto insertedTransaction = insertTransaction("debit");
    int notExistingTransactionId = insertedTransaction.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("Transaction with id='%s' not found", notExistingTransactionId);

    mockMvc.perform(delete("/transactions/{id}", notExistingTransactionId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void createTransactionWithIncorrectType_ReturnsErrorResponse() {
    String incorrectTransactionType = "incorrect-transaction-type";
    assertThrows(UnsupportedTransactionTypeException.class, () -> insertTransaction(incorrectTransactionType));
  }

}
