package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Todo;
import com.joe.dailymate.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ========== 新增接口 ==========

    /**
     * 按完成状态筛选（如获取全部未完成/已完成）
     */
    @GetMapping("/by-status")
    public List<Todo> getTodosByStatus(@RequestParam Long userId, @RequestParam Integer status) {
        return todoService.getTodoListByUserAndStatus(userId, status);
    }

    /**
     * 批量设置完成状态（批量完成/未完成）
     */
    @PutMapping("/batch/finish")
    public String batchUpdateStatus(@RequestBody Map<String, Object> req) {
        List<Integer> ids = (List<Integer>) req.get("ids");
        Integer newStatus = (Integer) req.get("status");
        todoService.batchUpdateStatus(ids.stream().map(Long::valueOf).toList(), newStatus);
        return "OK";
    }

    /**
     * 批量软删除
     */
    @PutMapping("/batch/delete")
    public String batchDelete(@RequestBody Map<String, Object> req) {
        List<Integer> ids = (List<Integer>) req.get("ids");
        todoService.batchDelete(ids.stream().map(Long::valueOf).toList());
        return "OK";
    }

    /**
     * 彻底物理删除
     */
    @DeleteMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id) {
        todoService.hardDelete(id);
        return "OK";
    }

    /**
     * 批量彻底物理删除
     */
    @PutMapping("/batch/hard-delete")
    public String batchHardDelete(@RequestBody Map<String, Object> req) {
        List<Integer> ids = (List<Integer>) req.get("ids");
        todoService.batchHardDelete(ids.stream().map(Long::valueOf).toList());
        return "OK";
    }

    /**
     * 恢复已软删除的待办
     */
    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        todoService.restore(id);
        return "OK";
    }

    /**
     * 模糊查找（按标题或内容）
     */
    @GetMapping("/search")
    public List<Todo> searchTodo(@RequestParam Long userId, @RequestParam String keyword) {
        return todoService.searchTodo(userId, keyword);
    }

    /**
     * 日期区间查询
     */
    @GetMapping("/range")
    public List<Todo> getTodosInRange(@RequestParam Long userId,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return todoService.getTodoListByUserAndDateRange(userId, start, end);
    }

    /**
     * 修改优先级
     */
    @PutMapping("/priority/{id}")
    public String updatePriority(@PathVariable Long id, @RequestBody Map<String, Integer> req) {
        Integer priority = req.get("priority");
        todoService.updatePriority(id, priority);
        return "OK";
    }

    /**
     * 获取未完成数量
     */
    @GetMapping("/unfinished-count")
    public long getUnfinishedCount(@RequestParam Long userId) {
        return todoService.countUnfinished(userId);
    }

    /**
     * 按优先级筛选
     */
    @GetMapping("/by-priority")
    public List<Todo> getByPriority(@RequestParam Long userId, @RequestParam Integer priority) {
        return todoService.getTodoListByUserAndPriority(userId, priority);
    }

}