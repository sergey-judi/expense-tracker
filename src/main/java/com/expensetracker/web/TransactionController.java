package com.expensetracker.web;

import com.expensetracker.converter.TransactionConverter;
import com.expensetracker.model.Transaction;
import com.expensetracker.service.transaction.TransactionService;
import com.expensetracker.web.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionConverter transactionConverter;

  @GetMapping
  public List<TransactionDto> getAllTransactions() {
    return transactionService.getAll().stream()
        .map(transactionConverter::toDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public TransactionDto getTransaction(@PathVariable Integer id) {
    Transaction retrievedTransaction = transactionService.getById(id);
    return transactionConverter.toDto(retrievedTransaction);
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public TransactionDto createTransaction(@Validated @RequestBody TransactionDto newTransactionDto) {
    Transaction givenTransaction = transactionConverter.toModel(newTransactionDto);
    Transaction createdTransaction = transactionService.createTransaction(givenTransaction);
    return transactionConverter.toDto(createdTransaction);
  }

  @PutMapping("/{id}")
  public TransactionDto updateTransaction(@PathVariable Integer id,
                                          @Validated @RequestBody TransactionDto updatedTransactionDto) {
    Transaction givenTransaction = transactionConverter.toModel(updatedTransactionDto);
    Transaction updatedTransaction = transactionService.updateTransactionById(id, givenTransaction);
    return transactionConverter.toDto(updatedTransaction);
  }

  @DeleteMapping("/{id}")
  public void deleteTransaction(@PathVariable Integer id) {
    transactionService.deleteTransactionById(id);
  }

}
