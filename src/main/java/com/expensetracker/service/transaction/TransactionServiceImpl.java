package com.expensetracker.service.transaction;

import com.expensetracker.exception.EntityNotFoundException;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.service.category.CategoryService;
import com.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository transactionRepository;
  private final CategoryService categoryService;
  private final UserService userService;

  @Override
  public List<Transaction> getAll() {
    return transactionRepository.findAll();
  }

  @Override
  public Transaction getById(Integer transactionId) {
    assertTransactionExists(transactionId);
    return transactionRepository.getById(transactionId);
  }

  @Override
  @Transactional
  public Transaction createTransaction(Transaction newTransaction) {
    Integer categoryId = newTransaction.getCategory().getId();
    Integer userId = newTransaction.getUser().getId();

    categoryService.assertCategoryExists(categoryId);
    userService.assertUserExists(userId);

    User userInDb = userService.getById(userId);
    Double transactionAmount = newTransaction.getAmount();

    if (newTransaction.getType() == TransactionType.DEBIT) {
      // balance -=
      userInDb.decreaseBalance(transactionAmount);
    } else {
      // balance +=
      userInDb.increaseBalance(transactionAmount);
    }

    return transactionRepository.save(newTransaction);
  }

  @Override
  @Transactional
  public Transaction updateTransactionById(Integer transactionId, Transaction updatedTransaction) {
    assertTransactionExists(transactionId);
    Transaction transactionInDb = transactionRepository.getById(transactionId);

    Integer categoryId = updatedTransaction.getCategory().getId();
    Integer userId = updatedTransaction.getUser().getId();

    categoryService.assertCategoryExists(categoryId);
    userService.assertUserExists(userId);

    User userInDb = transactionInDb.getUser();
    Double transactionAmount = updatedTransaction.getAmount() - transactionInDb.getAmount();

    if (updatedTransaction.getType() == TransactionType.DEBIT) {
      // balance -=
      userInDb.decreaseBalance(transactionAmount);
    } else {
      // balance +=
      userInDb.increaseBalance(transactionAmount);
    }

    updatedTransaction.setId(transactionId);
    return transactionRepository.save(updatedTransaction);
  }

  @Override
  @Transactional
  public void deleteTransactionById(Integer transactionId) {
    assertTransactionExists(transactionId);
    Transaction transactionInDb = transactionRepository.getById(transactionId);
    User userInDb = transactionInDb.getUser();
    Double transactionAmount = transactionInDb.getAmount();

    if (transactionInDb.getType() == TransactionType.DEBIT) {
      // balance +=
      userInDb.increaseBalance(transactionAmount);
    } else {
      // balance -=
      userInDb.decreaseBalance(transactionAmount);
    }

    transactionRepository.deleteById(transactionId);
  }

  @Override
  public void assertTransactionExists(Integer transactionId) {
    if (!transactionRepository.existsById(transactionId)) {
      throw new EntityNotFoundException(format("Transaction with id='%s' not found", transactionId));
    }
  }
}
