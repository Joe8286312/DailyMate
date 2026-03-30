package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Todo;
import com.joe.dailymate.service.TodoService;
import com.joe.dailymate.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping("/list")
    public Object getTodoList(HttpServletRequest request,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (date != null) {
            return todoService.getTodoListByUserAndDate(userId, date);
        }
        if (page != null) {
            PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date", "id"));
            return todoService.getTodoListByUser(userId, pageable);
        }
        return todoService.getTodoListByUser(userId);
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Todo todo = todoService.findById(id);
        if (todo == null || !todo.getUserId().equals(currentUserId)) return null;
        return todo;
    }

    @PostMapping("/add")
    public Todo addTodo(@RequestBody Todo todo, HttpServletRequest request) {
        todo.setUserId(JwtUtil.getUserIdFromRequest(request));
        return todoService.addTodo(todo);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTodo(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Todo todo = todoService.findById(id);
        if (todo != null && todo.getUserId().equals(currentUserId)) {
            todoService.deleteTodo(id);
        }
    }

    @PutMapping("/update")
    public Todo updateTodo(@RequestBody Todo todo, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Todo existing = todoService.findById(todo.getId());
        if (existing == null || !existing.getUserId().equals(currentUserId)) return null;
        todo.setUserId(currentUserId);
        return todoService.updateTodo(todo);
    }

    @GetMapping("/by-status")
    public List<Todo> getTodosByStatus(HttpServletRequest request, @RequestParam Integer status) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return todoService.getTodoListByUserAndStatus(userId, status);
    }

    @PutMapping("/batch/finish")
    public String batchUpdateStatus(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        List<Integer> ids = (List<Integer>) req.get("ids");
        Integer newStatus = (Integer) req.get("status");
        List<Long> ownedIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> {
                    Todo todo = todoService.findById(id);
                    return todo != null && todo.getUserId().equals(currentUserId);
                })
                .toList();
        todoService.batchUpdateStatus(ownedIds, newStatus);
        return "OK";
    }

    @PutMapping("/batch/delete")
    public String batchDelete(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        List<Integer> ids = (List<Integer>) req.get("ids");
        List<Long> ownedIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> {
                    Todo todo = todoService.findById(id);
                    return todo != null && todo.getUserId().equals(currentUserId);
                })
                .toList();
        todoService.batchDelete(ownedIds);
        return "OK";
    }

    @DeleteMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Todo todo = todoService.findById(id);
        if (todo != null && todo.getUserId().equals(currentUserId)) {
            todoService.hardDelete(id);
        }
        return "OK";
    }

    @PutMapping("/batch/hard-delete")
    public String batchHardDelete(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        List<Integer> ids = (List<Integer>) req.get("ids");
        List<Long> ownedIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> {
                    Todo todo = todoService.findById(id);
                    return todo != null && todo.getUserId().equals(currentUserId);
                })
                .toList();
        todoService.batchHardDelete(ownedIds);
        return "OK";
    }

    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        todoService.restore(id);
        return "OK";
    }

    @GetMapping("/search")
    public List<Todo> searchTodo(HttpServletRequest request, @RequestParam String keyword) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return todoService.searchTodo(userId, keyword);
    }

    @GetMapping("/range")
    public List<Todo> getTodosInRange(HttpServletRequest request,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return todoService.getTodoListByUserAndDateRange(userId, start, end);
    }

    @PutMapping("/priority/{id}")
    public String updatePriority(@PathVariable Long id, @RequestBody Map<String, Integer> req,
                                 HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Todo todo = todoService.findById(id);
        if (todo != null && todo.getUserId().equals(currentUserId)) {
            todoService.updatePriority(id, req.get("priority"));
        }
        return "OK";
    }

    @GetMapping("/unfinished-count")
    public long getUnfinishedCount(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return todoService.countUnfinished(userId);
    }

    @GetMapping("/by-priority")
    public List<Todo> getByPriority(HttpServletRequest request, @RequestParam Integer priority) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return todoService.getTodoListByUserAndPriority(userId, priority);
    }

}