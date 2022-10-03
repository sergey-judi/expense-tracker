package com.expensetracker.web.util;

import com.expensetracker.converter.CategoryConverter;
import com.expensetracker.converter.TransactionConverter;
import com.expensetracker.converter.UserConverter;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import com.expensetracker.web.dto.CategoryDto;
import com.expensetracker.web.dto.TransactionDto;
import com.expensetracker.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransactionEntityProvider {

  private final CategoryConverter categoryConverter;
  private final UserConverter userConverter;
  private final TransactionConverter transactionConverter;

  public TransactionDto prepareTransactionDto(UserDto userDto, CategoryDto categoryDto, String type) {
    Integer id = null;
    Integer userId = userDto.getId();
    Integer categoryId = categoryDto.getId();
    Double amount = new Random().nextDouble() * 100;
    Date time = new Date();

    return new TransactionDto(id, userId, categoryId, type, amount, time);
  }

  public Transaction prepareTransactionModel(User user, Category category, String type) {
    return transactionConverter.toModel(
        prepareTransactionDto(
            userConverter.toDto(user),
            categoryConverter.toDto(category),
            type
        )
    );
  }

}
