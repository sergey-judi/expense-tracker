package com.expensetracker.web;

import com.expensetracker.exception.EntityAlreadyExistsException;
import com.expensetracker.exception.EntityNotFoundException;
import com.expensetracker.exception.ErrorCode;
import com.expensetracker.exception.UnsupportedTransactionTypeException;
import com.expensetracker.exception.model.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.expensetracker.exception.ErrorCode.ENTITY_ALREADY_EXISTS;
import static com.expensetracker.exception.ErrorCode.ENTITY_NOT_FOUND;
import static com.expensetracker.exception.ErrorCode.INTERNAL;
import static com.expensetracker.exception.ErrorCode.INVALID_PARAMS;
import static com.expensetracker.exception.ErrorCode.UNSUPPORTED_TRANSACTION_TYPE;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler({
      EntityAlreadyExistsException.class,
      UnsupportedTransactionTypeException.class,
      MethodArgumentNotValidException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleAlreadyExists(Exception ex) {
    if (ex instanceof EntityAlreadyExistsException) {
      return buildErrorResponse(ENTITY_ALREADY_EXISTS, ex.getMessage());
    } else if (ex instanceof MethodArgumentNotValidException) {
      return buildValidationErrorResponse((MethodArgumentNotValidException) ex);
    } else if (ex instanceof UnsupportedTransactionTypeException) {
      return buildErrorResponse(UNSUPPORTED_TRANSACTION_TYPE, ex.getMessage());
    }
    return buildErrorResponse(INTERNAL, ex.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(EntityNotFoundException ex) {
    return buildErrorResponse(ENTITY_NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleServerError(Exception ex) {
    return buildErrorResponse(INTERNAL, ex.getMessage());
  }

  private ErrorResponse buildErrorResponse(ErrorCode errorCode, String message) {
    return ErrorResponse.builder()
        .code(errorCode.getCode())
        .message(message)
        .build();
  }

  private ErrorResponse buildValidationErrorResponse(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getGlobalErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .sorted()
        .collect(Collectors.toList());

    Map<String, List<String>> validation = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> Map.entry(error.getField(), error.getDefaultMessage()))
        .collect(Collectors.groupingBy(
            Map.Entry::getKey,
            Collectors.mapping(
                Map.Entry::getValue,
                Collectors.toList()
            )));

    errors.addAll(
        validation.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
            .sorted()
            .collect(Collectors.toList())
    );

    String errorMessage = String.join(", ", errors);

    return ErrorResponse.builder()
        .code(INVALID_PARAMS.getCode())
        .message(errorMessage)
        .validation(validation)
        .build();
  }

}
