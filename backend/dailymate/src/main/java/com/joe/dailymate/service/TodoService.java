// TodoService.java
package com.joe.dailymate.service;

import com.joe.dailymate.entity.Todo;
import java.util.Date;
import java.util.List;

public interface TodoService {
    List<Todo> getTodoListByUserAndDate(Long userId, Date date);
    List<Todo> getTodoListByUser(Long userId);
    Todo addTodo(Todo todo);
    void deleteTodo(Long id);
    Todo updateTodo(Todo todo);
    Todo findById(Long id);
}