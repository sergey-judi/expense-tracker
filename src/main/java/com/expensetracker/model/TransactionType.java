package com.expensetracker.model;

import com.expensetracker.exception.UnsupportedTransactionTypeException;

import static java.util.Objects.isNull;

public enum TransactionType {
  DEBIT("debit"),
  CREDIT("credit");

  private String type;

  TransactionType(String type) {
    this.type = type;
  }

  public String getCanonicalType() {
    return this.type;
  }

  public static TransactionType fromString(String type) {
    if (isNull(type)) {
      throw new UnsupportedTransactionTypeException("Transaction type can not be null");
    }

    for (TransactionType transactionType : values()) {
      if (transactionType.getCanonicalType().equals(type)) {
        return transactionType;
      }
    }

    String message = String.format("Received unsupported transaction type [%s]", type);
    throw new UnsupportedTransactionTypeException(message);
  }
}
