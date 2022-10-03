package com.expensetracker.web.dto;

import com.expensetracker.model.TransactionType;
import com.expensetracker.web.validation.CorrectTransactionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.util.Date;

import static java.util.Objects.isNull;

@Value
public class TransactionDto {

  @Null
  Integer id;

  @NotNull
  Integer userId;

  @NotNull
  Integer categoryId;

  @NotNull
  TransactionType type;

  @NotNull
  @Positive
  Double amount;

  Date time;

  @JsonCreator
  public TransactionDto(@JsonProperty("id") Integer id,
                        @JsonProperty("userId") Integer userId,
                        @JsonProperty("categoryId") Integer categoryId,
                        @JsonProperty("type") @CorrectTransactionType String type,
                        @JsonProperty("amount") Double amount,
                        @JsonProperty("time") Date time) {
    this.id = id;
    this.userId = userId;
    this.categoryId = categoryId;
    this.type = TransactionType.fromString(type);
    this.amount = amount;
    this.time = isNull(time) ? new Date() : time;
  }
}
