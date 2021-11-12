package com.expensetracker.converter;

import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import com.expensetracker.web.dto.TransactionDto;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter implements Converter<TransactionDto, Transaction> {
  @Override
  public Transaction toModel(TransactionDto dto) {
    return new Transaction(
        dto.getId(),
        User.builder().id(dto.getUserId()).build(),
        dto.getType(),
        dto.getAmount(),
        dto.getTime()
    );
  }

  @Override
  public TransactionDto toDto(Transaction model) {
    return new TransactionDto(
        model.getId(),
        model.getUser().getId(),
        model.getType().getCanonicalType(),
        model.getAmount(),
        model.getTime()
    );
  }
}
