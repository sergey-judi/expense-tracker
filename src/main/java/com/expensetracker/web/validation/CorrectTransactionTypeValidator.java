package com.expensetracker.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class CorrectTransactionTypeValidator implements ConstraintValidator<CorrectTransactionType, String> {

  private String[] availableTypes;

  @Override
  public void initialize(CorrectTransactionType constraintAnnotation) {
    this.availableTypes = constraintAnnotation.availableTypes();
  }

  @Override
  public boolean isValid(String transactionType, ConstraintValidatorContext constraintValidatorContext) {
    System.out.println(transactionType);
    return List.of(availableTypes).contains(transactionType);
  }
}
