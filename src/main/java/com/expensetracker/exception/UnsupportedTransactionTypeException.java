package com.expensetracker.exception;

public class UnsupportedTransactionTypeException extends RuntimeException {
  public UnsupportedTransactionTypeException(String message) {
    super(message);
  }
}
