package com.expensetracker.web.dto;

import com.expensetracker.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.util.Date;

@Value
public class TransactionDto {

  @Null
  Integer id;

  @NotNull
  Integer userId;

  @NotNull
  TransactionType type;

  @NotNull
  @Positive
  Double amount;

  Date time;

  @JsonCreator
  public TransactionDto(@JsonProperty("id") Integer id,
                        @JsonProperty("userId") Integer userId,
                        @JsonProperty("type") String type,
                        @JsonProperty("amount") Double amount,
                        @JsonProperty("time") Date time) {
    this.id = id;
    this.userId = userId;
    this.type = TransactionType.fromString(type);
    this.amount = amount;
    this.time = time;
  }
}
