package com.expensetracker.converter;

import com.expensetracker.model.Transaction;
import com.expensetracker.web.dto.TransactionDto;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter implements Converter<TransactionDto, Transaction> {
  @Override
  public Transaction toModel(TransactionDto dto) {
    return null;
  }

  @Override
  public TransactionDto toDto(Transaction model) {
    return null;
  }
}
