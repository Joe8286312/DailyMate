package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Todo;
import com.joe.dailymate.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;

//    @GetMapping("/list")
//    public List<Todo> getTodoList(@RequestParam Long userId, @RequestParam(required = false) Date date) {
//        if (date != null) {
//            return todoService.getTodoListByUserAndDate(userId, date);
//        } else {
//            return todoService.getTodoListByUser(userId);
//        }
//    }


    @GetMapping("/list")
    public List<Todo> getTodoList(@RequestParam Long userId,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        if (date != null) {
            return todoService.getTodoListByUserAndDate(userId, date);
        } else {
            return todoService.getTodoListByUser(userId);
        }
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable Long id) {
        return todoService.findById(id);
    }

    @PostMapping("/add")
    public Todo addTodo(@RequestBody Todo todo) {
        return todoService.addTodo(todo);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/update")
    public Todo updateTodo(@RequestBody Todo todo) {
        return todoService.updateTodo(todo);
    }
}