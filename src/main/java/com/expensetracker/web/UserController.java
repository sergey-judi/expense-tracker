package com.expensetracker.web;

import com.expensetracker.converter.UserConverter;
import com.expensetracker.model.User;
import com.expensetracker.service.user.UserService;
import com.expensetracker.web.dto.UserDto;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserConverter userConverter;

  @GetMapping
  public List<UserDto> getAllUsers() {
    return userService.getAll().stream()
        .map(userConverter::toDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public UserDto getUser(@PathVariable Integer id) {
    User retrievedUser = userService.getById(id);
    return userConverter.toDto(retrievedUser);
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public UserDto createUser(@Validated @RequestBody UserDto newUserDto) {
    User givenUser = userConverter.toModel(newUserDto);
    User createdUser = userService.createUser(givenUser);
    return userConverter.toDto(createdUser);
  }

  @PutMapping("/{id}")
  public UserDto updateUser(@PathVariable Integer id,
                            @Validated @RequestBody UserDto updatedUserDto) {
    User givenUser = userConverter.toModel(updatedUserDto);
    User updatedUser = userService.updateUserById(id, givenUser);
    return userConverter.toDto(updatedUser);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Integer id) {
    userService.deleteUserById(id);
  }
}
