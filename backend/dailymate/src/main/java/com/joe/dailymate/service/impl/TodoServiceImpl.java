package com.joe.dailymate.service.impl;

import com.joe.dailymate.entity.Todo;
import com.joe.dailymate.repository.TodoRepository;
import com.joe.dailymate.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoRepository todoRepository;

    // 只查未删除
    @Override
    public List<Todo> getTodoListByUser(Long userId) {
        return todoRepository.findByUserIdAndIsDelete(userId, 0);
    }

    @Override
    public List<Todo> getTodoListByUserAndDate(Long userId, Date date) {
        return todoRepository.findByUserIdAndDateAndIsDelete(userId, date, 0);
    }

    @Override
    public Todo addTodo(Todo todo) {
        todo.setIsDelete(0);
        return todoRepository.save(todo);
    }

    // 软删除替换硬删除
    @Override
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            todo.setIsDelete(1);
            todoRepository.save(todo);
        }
        // todoRepository.deleteById(id); // 被软删除替代
    }

    @Override
    public Todo updateTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public Todo findById(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            return todo;
        }
        return null;
    }
}