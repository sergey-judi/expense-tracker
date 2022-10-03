package com.expensetracker.web;

import com.expensetracker.exception.ErrorCode;
import com.expensetracker.web.dto.UserDto;
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

class UserControllerTest extends AbstractBaseControllerTest {

  @Test
  @SneakyThrows
  void getAllUsers() {
    int insertedUsersAmount = 3;

    for (int i = 0; i < insertedUsersAmount; i++) {
      insertUser();
    }

    MvcResult mvcResult = mockMvc.perform(get("/users"))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    List<UserDto> userDtos = objectMapper.readValue(responseBody, new TypeReference<>() {});

    assertTrue(userDtos.size() >= insertedUsersAmount);
  }

  @Test
  @SneakyThrows
  void getUserById() {
    UserDto insertedUser = insertUser();

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(insertedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(insertedUser.getEmail()))
        .andExpect(jsonPath("$.balance").value(insertedUser.getBalance()));
  }

  @Test
  @SneakyThrows
  void getNotExistingUserById_ReturnsErrorResponse() {
    UserDto insertedUser = insertUser();
    int notExistingUserId = insertedUser.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("User with id='%s' not found", notExistingUserId);

    mockMvc.perform(get("/users/{id}", notExistingUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void createUser() {
    UserDto newUser = userEntityProvider.prepareUserDto();

    MvcResult mvcResult = mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newUser)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, UserDto.class);

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(insertedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(insertedUser.getEmail()))
        .andExpect(jsonPath("$.balance").value(insertedUser.getBalance()));
  }

  @Test
  @SneakyThrows
  void createUserWithDefaultBalance() {
    UserDto newUser = new UserDto(
        null,
        "user-default-balance-full-name",
        "user-default-balance-email",
        null
    );

    MvcResult mvcResult = mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newUser)))
        .andExpect(status().isCreated())
        .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    UserDto insertedUser = objectMapper.readValue(responseBody, UserDto.class);

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(insertedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(insertedUser.getEmail()))
        .andExpect(jsonPath("$.balance").value(0.0));
  }

  @Test
  @SneakyThrows
  void createUserWithExistingEmail_ReturnsErrorResponse() {
    UserDto newUser = userEntityProvider.prepareUserDto();

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newUser)))
        .andExpect(status().isCreated());

    String expectedErrorCode = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage = String.format(
        "Wasn't able to create new user. User with email='%s' already exists",
        newUser.getEmail()
    );

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(newUser)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void updateUserById() {
    UserDto insertedUser = insertUser();
    UserDto updatedUser = userEntityProvider.prepareUserDto();

    mockMvc.perform(put("/users/{id}", insertedUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(updatedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
        .andExpect(jsonPath("$.balance").value(updatedUser.getBalance()));

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(updatedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
        .andExpect(jsonPath("$.balance").value(updatedUser.getBalance()));
  }

  @Test
  @SneakyThrows
  void updateNotExistingUserById_ReturnsErrorResponse() {
    UserDto insertedUser = insertUser();
    UserDto updatedUser = userEntityProvider.prepareUserDto();
    int notExistingUserId = insertedUser.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("User with id='%s' not found", notExistingUserId);

    mockMvc.perform(put("/users/{id}", notExistingUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedUser)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

  @Test
  @SneakyThrows
  void updateUserByIdWithExistingEmail_ReturnsErrorResponse() {
    UserDto insertedUser1 = insertUser();
    UserDto insertedUser2 = insertUser();

    UserDto updatedUser1 = new UserDto(
        null,
        insertedUser2.getFullName(),
        insertedUser2.getEmail(),
        null
    );

    UserDto updatedUser2 = new UserDto(
        null,
        insertedUser1.getFullName(),
        insertedUser1.getEmail(),
        null
    );

    String expectedErrorCode1 = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage1 = String.format(
        "Wasn't able to update existing user with id='%s'. User with email='%s' already exists",
        insertedUser1.getId(), updatedUser1.getEmail()
    );

    mockMvc.perform(put("/users/{id}", insertedUser1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedUser1)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode1))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage1));

    String expectedErrorCode2 = ErrorCode.ENTITY_ALREADY_EXISTS.getCode();
    String expectedErrorMessage2 = String.format(
        "Wasn't able to update existing user with id='%s'. User with email='%s' already exists",
        insertedUser2.getId(), updatedUser2.getEmail()
    );

    mockMvc.perform(put("/users/{id}", insertedUser2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(serialize(updatedUser2)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(expectedErrorCode2))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage2));
  }

  @Test
  @SneakyThrows
  void deleteUserById() {
    UserDto insertedUser = insertUser();

    mockMvc.perform(delete("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void deleteNotExistingUserById_ReturnsErrorResponse() {
    UserDto insertedUser = insertUser();
    int notExistingUserId = insertedUser.getId() + 1;

    String expectedErrorCode = ErrorCode.ENTITY_NOT_FOUND.getCode();
    String expectedErrorMessage = String.format("User with id='%s' not found", notExistingUserId);

    mockMvc.perform(delete("/users/{id}", notExistingUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(expectedErrorCode))
        .andExpect(jsonPath("$.message").value(expectedErrorMessage));
  }

}
