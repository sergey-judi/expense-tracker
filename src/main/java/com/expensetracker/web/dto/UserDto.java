package com.expensetracker.web.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Value
public class UserDto {

  @Null
  Integer id;

  @NotBlank
  String fullName;

  @NotBlank
  String email;

}
