package com.expensetracker.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CorrectTransactionTypeValidator.class)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectTransactionType {
  String message() default "transaction type must be 'debit' or 'credit'";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  String[] availableTypes() default {"debit", "credit"};
}
