package com.expensetracker.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import static java.util.Objects.isNull;

@Value
public class UserDto {

  @Null
  Integer id;

  @NotBlank
  String fullName;

  @NotBlank
  String email;

  Double balance;

  @JsonCreator
  public UserDto(@JsonProperty("id") Integer id,
                 @JsonProperty("fullName") String fullName,
                 @JsonProperty("email") String email,
                 @JsonProperty("balance") Double balance) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.balance = isNull(balance) ? 0.0 : balance;
  }
}
