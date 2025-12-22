// UserService.java
package com.joe.dailymate.service;

import com.joe.dailymate.entity.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User saveUser(User user);
    List<User> findAll();
    User findById(Long id);
    void deleteById(Long id);
}