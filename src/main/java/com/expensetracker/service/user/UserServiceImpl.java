package com.expensetracker.service.user;

import com.expensetracker.exception.EntityAlreadyExistsException;
import com.expensetracker.exception.EntityNotFoundException;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public List<User> getAll() {
    return userRepository.findAll();
  }

  @Override
  public User getById(Integer userId) {
    assertUserExists(userId);
    return userRepository.getById(userId);
  }

  @Override
  public User createUser(User newUser) {
    if (userRepository.existsByEmail(newUser.getEmail())) {
      String message = format("Wasn't able to create new user. User with email='%s' already exists", newUser.getEmail());
      throw new EntityAlreadyExistsException(message);
    }
    return userRepository.save(newUser);
  }

  @Override
  public User updateUserById(Integer userId, User updatedUser) {
    assertUserExists(userId);
    User userInDb = userRepository.findByEmail(updatedUser.getEmail());

    if (Objects.nonNull(userInDb) && !Objects.equals(userId, userInDb.getId())) {
      String message = format(
          "Wasn't able to update existing user with id='%s'. User with email='%s' already exists",
          userId, updatedUser.getEmail()
      );
      throw new EntityAlreadyExistsException(message);
    }

    updatedUser.setId(userId);
    return userRepository.save(updatedUser);
  }

  @Override
  public void deleteUserById(Integer userId) {
    assertUserExists(userId);
    userRepository.deleteById(userId);
  }

  @Override
  public void assertUserExists(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException(format("User with id='%s' not found", userId));
    }
  }
}
