package com.expensetracker.web.thymeleaf;

import com.expensetracker.model.Transaction;
import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.TransactionDto;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/web/transactions")
public class TransactionWebController {

  private final HttpService<TransactionDto> httpService;

  private final String TRANSACTION_URI;

  public TransactionWebController(ServerProperties serverProperties, HttpService<TransactionDto> httpService) {
    this.TRANSACTION_URI = "http://localhost:" + serverProperties.getPort() + "/transactions";
    this.httpService = httpService;
  }

  @GetMapping
  public String getAllTransactions(Model model) {
    model.addAttribute("transactions", httpService.get(TRANSACTION_URI, List.class));
    return "transactions/index";
  }

  @GetMapping("/add")
  public String createTransactionForm(Model model) {
    TransactionDto newTransaction = new TransactionDto(null, null, null, "debit", null, null);
    model.addAttribute("newTransaction", newTransaction);
    return "transactions/create";
  }

  @PostMapping
  public String createTransaction(@ModelAttribute TransactionDto transaction) {
    httpService.post(transaction, TRANSACTION_URI);
    return "redirect:/web/transactions";
  }

  @GetMapping("/edit/{id}")
  public String updateTransactionForm(@PathVariable Integer id, Model model) {
    TransactionDto transactionDto = httpService.get(TRANSACTION_URI + "/" + id, TransactionDto.class);
    model.addAttribute("transaction", transactionDto);
    return "transactions/edit";
  }

  @GetMapping("/{id}")
  public String updateTransaction(@PathVariable Integer id, @ModelAttribute TransactionDto transaction) {
    TransactionDto transactionDto = new TransactionDto(
        null,
        transaction.getUserId(),
        transaction.getCategoryId(),
        transaction.getType().getCanonicalType(),
        transaction.getAmount(),
        null
    );
    httpService.put(id, transactionDto, TRANSACTION_URI);
    return "redirect:/web/transactions";
  }

  @GetMapping("/delete/{id}")
  public String deleteTransaction(@PathVariable Integer id) {
    httpService.delete(id, TRANSACTION_URI);
    return "redirect:/web/transactions";
  }

}
