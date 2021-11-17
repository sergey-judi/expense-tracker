package com.expensetracker.web.thymeleaf;

import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.CategoryDto;
import com.expensetracker.web.dto.TransactionDto;
import com.expensetracker.web.dto.UserDto;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(path = {"/", "/web"})
public class MainWebController {

  private final HttpService<Object> httpService;

  private final String CATEGORY_URI;
  private final String TRANSACTION_URI;
  private final String USER_URI;

  public MainWebController(ServerProperties serverProperties, HttpService<Object> httpService) {
    this.CATEGORY_URI = "http://localhost:" + serverProperties.getPort() + "/categories";
    this.TRANSACTION_URI = "http://localhost:" + serverProperties.getPort() + "/transactions";
    this.USER_URI = "http://localhost:" + serverProperties.getPort() + "/users";
    this.httpService = httpService;
  }

  @GetMapping
  public String getAllInfo(Model model) {
    List<CategoryDto> categories = httpService.get(CATEGORY_URI, List.class);
    List<TransactionDto> transactions = httpService.get(TRANSACTION_URI, List.class);
    List<UserDto> users = httpService.get(USER_URI, List.class);

    model.addAttribute("categoriesAmount", categories.size());
    model.addAttribute("transactionsAmount", transactions.size());
    model.addAttribute("usersAmount", users.size());

    return "index";
  }
}
