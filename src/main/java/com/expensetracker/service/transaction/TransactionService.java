package com.expensetracker.service.transaction;

import com.expensetracker.model.Transaction;

import java.util.List;

public interface TransactionService {

  List<Transaction> getAll();
  List<Transaction> getAllByUserId(Integer userId);
  Transaction getById(Integer transactionId);
  Transaction createTransaction(Transaction newTransaction);
  Transaction updateTransactionById(Integer transactionId, Transaction updatedTransaction);
  void deleteTransactionById(Integer transactionId);
  void assertTransactionExists(Integer transactionId);

}
