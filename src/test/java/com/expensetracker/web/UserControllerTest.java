package com.expensetracker.web;

import com.expensetracker.web.dto.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractBaseControllerTest {

  @Test
  @SneakyThrows
  void getUserById() {
    UserDto insertedUser = insertUser();

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(insertedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(insertedUser.getEmail()));
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
        .andExpect(jsonPath("$.email").value(insertedUser.getEmail()));
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
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

    mockMvc.perform(get("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(insertedUser.getId()))
        .andExpect(jsonPath("$.fullName").value(updatedUser.getFullName()))
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));
  }

  @Test
  @SneakyThrows
  void deleteUserById() {
    UserDto insertedUser = insertUser();

    mockMvc.perform(delete("/users/{id}", insertedUser.getId()))
        .andExpect(status().isOk());
  }

}