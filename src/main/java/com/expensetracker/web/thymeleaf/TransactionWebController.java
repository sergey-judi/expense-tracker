package com.expensetracker.web.thymeleaf;

import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.CategoryDto;
import com.expensetracker.web.dto.TransactionDto;
import com.expensetracker.web.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/transactions")
public class TransactionWebController {

  private final HttpService<TransactionDto> httpService;

  private final String TRANSACTION_URI;
  private final String CATEGORY_URI;
  private final String USER_URI;

  private final ObjectMapper objectMapper;

  public TransactionWebController(ServerProperties serverProperties, HttpService<TransactionDto> httpService, ObjectMapper objectMapper) {
    this.TRANSACTION_URI = "http://localhost:" + serverProperties.getPort() + "/transactions";
    this.CATEGORY_URI = "http://localhost:" + serverProperties.getPort() + "/categories";
    this.USER_URI = "http://localhost:" + serverProperties.getPort() + "/users";
    this.httpService = httpService;
    this.objectMapper = objectMapper;
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
    model.addAttribute("userIds", getUserIds());
    model.addAttribute("categories", httpService.get(CATEGORY_URI, List.class));
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
    model.addAttribute("categories", httpService.get(CATEGORY_URI, List.class));
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

  private List<Integer> getUserIds() {
    List<UserDto> users = objectMapper.convertValue(httpService.get(USER_URI, List.class), new TypeReference<>() {});
    return users.stream()
        .map(UserDto::getId)
        .collect(Collectors.toList());
  }

}
