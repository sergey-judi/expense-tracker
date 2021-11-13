package com.expensetracker.exception;

public enum ErrorCode {
  INTERNAL("expense.tracker.internal-error"),
  INVALID_PARAMS("expense.tracker.invalid-params"),

  ENTITY_NOT_FOUND("expense.tracker.entity.not-found"),
  ENTITY_ALREADY_EXISTS("expense.tracker.entity.already-exists"),

  UNSUPPORTED_TRANSACTION_TYPE("expense.tracker.transaction.unsupported-type");

  String code;

  ErrorCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }
}
