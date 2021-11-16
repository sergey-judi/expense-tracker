package com.expensetracker.web.thymeleaf;

import com.expensetracker.model.User;
import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.UserDto;
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

  public UserWebController(ServerProperties serverProperties, HttpService<UserDto> httpService) {
    this.USER_URI = "http://localhost:" + serverProperties.getPort() + "/users";
    this.httpService = httpService;
  }

  @GetMapping
  public String getAllUsers(Model model) {
    model.addAttribute("users", httpService.get(USER_URI, List.class));
    return "users/index";
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
}
