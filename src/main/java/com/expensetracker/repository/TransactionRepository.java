package com.expensetracker.repository;

import com.expensetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  List<Transaction> findAllByUserId(Integer userId);

}
