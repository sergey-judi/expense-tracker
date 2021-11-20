package com.expensetracker.web.thymeleaf;

import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.HttpService;
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

@Controller
@RequestMapping("/web/users")
public class UserWebController {

  private final HttpService<UserDto> httpService;

  private final String USER_URI;
  private final String TRANSACTION_URI;

  private final ObjectMapper objectMapper;

  public UserWebController(ServerProperties serverProperties, HttpService<UserDto> httpService, ObjectMapper objectMapper) {
    this.USER_URI = "http://localhost:" + serverProperties.getPort() + "/users";
    this.TRANSACTION_URI = "http://localhost:" + serverProperties.getPort() + "/transactions";
    this.httpService = httpService;
    this.objectMapper = objectMapper;
  }

  @GetMapping
  public String getAllUsers(Model model) {
    model.addAttribute("users", httpService.get(USER_URI, List.class));
    return "users/index";
  }

  @GetMapping("/{id}/transactions")
  public String getAllTransactionsForUser(@PathVariable Integer id, Model model) {
    List<TransactionDto> transactions = objectMapper.convertValue(httpService.get(TRANSACTION_URI + "/user/" + id, List.class), new TypeReference<>() {});
    Double totalDebit = getTotalDebitCommitted(transactions);
    Double totalCredit = getTotalCreditCommitted(transactions);

    model.addAttribute("transactions", transactions);
    model.addAttribute("totalDebit", totalDebit);
    model.addAttribute("totalCredit", totalCredit);
    return "transactions/list";
  }

  @GetMapping("/add")
  public String createUserForm(Model model) {
    User newUser = new User();
    model.addAttribute("newUser", newUser);
    return "users/create";
  }

  @PostMapping
  public String createUser(@ModelAttribute("user") UserDto user) {
    httpService.post(user, USER_URI);
    return "redirect:/web/users";
  }

  @GetMapping("/edit/{id}")
  public String updateUserForm(@PathVariable Integer id, Model model) {
    UserDto userDto = httpService.get(USER_URI + "/" + id, UserDto.class);
    model.addAttribute("user", userDto);
    return "users/edit";
  }

  @GetMapping("/{id}")
  public String updateUser(@PathVariable Integer id, @ModelAttribute("user") UserDto user) {
    UserDto userDto = new UserDto(null, user.getFullName(), user.getEmail(), user.getBalance());
    httpService.put(id, userDto, USER_URI);
    return "redirect:/web/users";
  }

  @GetMapping("/delete/{id}")
  public String deleteUser(@PathVariable Integer id) {
    httpService.delete(id, USER_URI);
    return "redirect:/web/users";
  }

  private Double getTotalDebitCommitted(List<TransactionDto> transactions) {
    return transactions.stream()
        .filter(transaction -> transaction.getType() == TransactionType.DEBIT)
        .mapToDouble(TransactionDto::getAmount)
        .sum();
  }

  private Double getTotalCreditCommitted(List<TransactionDto> transactions) {
    return transactions.stream()
        .filter(transaction -> transaction.getType() == TransactionType.CREDIT)
        .mapToDouble(TransactionDto::getAmount)
        .sum();
  }

}
