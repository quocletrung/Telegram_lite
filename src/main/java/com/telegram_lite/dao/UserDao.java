package com.telegram_lite.dao;

import com.telegram_lite.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(Long userId);

    Optional<User> findUserById(Long userId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    List<User> findAllUsers();

    List<User> searchUsers(String searchTerm, String usernameToExclude);

}