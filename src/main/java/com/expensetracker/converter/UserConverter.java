package com.expensetracker.converter;

import com.expensetracker.model.User;
import com.expensetracker.web.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<UserDto, User> {
  @Override
  public User toModel(UserDto dto) {
    return new User(
        dto.getId(),
        dto.getFullName(),
        dto.getEmail()
    );
  }

  @Override
  public UserDto toDto(User model) {
    return new UserDto(
        model.getId(),
        model.getFullName(),
        model.getEmail()
    );
  }
}
