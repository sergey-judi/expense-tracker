package com.expensetracker.web.util;

import com.expensetracker.converter.UserConverter;
import com.expensetracker.model.User;
import com.expensetracker.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEntityProvider {

  private final UserConverter userConverter;

  public UserDto prepareUserDto() {
    long currentTime = System.currentTimeMillis();

    Integer id = null;
    String fullName = String.format("user-%s-full-name", currentTime);
    String email = String.format("user-%s-email", currentTime);

    return new UserDto(id, fullName, email);
  }

  public User preapareUserModel() {
    return userConverter.toModel(prepareUserDto());
  };

}
