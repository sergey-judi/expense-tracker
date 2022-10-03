package com.expensetracker.service.user;

import com.expensetracker.model.User;

import java.util.List;

public interface UserService {

  List<User> getAll();
  User getById(Integer userId);
  User createUser(User newUser);
  User updateUserById(Integer userId, User updatedUser);
  void deleteUserById(Integer userId);
  void assertUserExists(Integer userId);

}
