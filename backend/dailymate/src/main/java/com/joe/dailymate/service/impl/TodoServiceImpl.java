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

    // ===== 新增功能实现 =====

    // 1. 按状态筛选（如完成/未完成）
    @Override
    public List<Todo> getTodoListByUserAndStatus(Long userId, Integer status) {
        return todoRepository.findByUserIdAndStatusAndIsDelete(userId, status, 0);
    }

    // 2. 批量状态变更（如批量完成/批量未完成）
    @Override
    public void batchUpdateStatus(List<Long> ids, Integer newStatus) {
        List<Todo> list = todoRepository.findAllById(ids);
        for (Todo todo : list) {
            if (todo.getIsDelete() != null && todo.getIsDelete() == 0) {
                todo.setStatus(newStatus);
            }
        }
        todoRepository.saveAll(list);
    }

    // 3. 批量软删除
    @Override
    public void batchDelete(List<Long> ids) {
        List<Todo> list = todoRepository.findAllById(ids);
        for (Todo todo : list) {
            if (todo.getIsDelete() != null && todo.getIsDelete() == 0) {
                todo.setIsDelete(1);
            }
        }
        todoRepository.saveAll(list);
    }

    // 4. 彻底物理删除
    @Override
    public void hardDelete(Long id) {
        todoRepository.deleteById(id);
    }

    // 5. 批量彻底物理删除
    @Override
    public void batchHardDelete(List<Long> ids) {
        todoRepository.deleteAllById(ids);
    }

    // 6. 恢复已软删除
    @Override
    public void restore(Long id) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 1) {
            todo.setIsDelete(0);
            todoRepository.save(todo);
        }
    }

    // 7. 模糊搜索
    @Override
    public List<Todo> searchTodo(Long userId, String keyword) {
        // 要在TodoRepository里补充定义
        return todoRepository.findByUserIdAndIsDeleteAndTitleContainingOrContentContaining(
                userId, 0, keyword, keyword);
    }

    // 8. 日期区间
    @Override
    public List<Todo> getTodoListByUserAndDateRange(Long userId, Date startDate, Date endDate) {
        return todoRepository.findByUserIdAndIsDeleteAndDateBetween(userId, 0, startDate, endDate);
    }

    // 9. 修改优先级
    @Override
    public void updatePriority(Long id, Integer priority) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo != null && todo.getIsDelete() != null && todo.getIsDelete() == 0) {
            todo.setPriority(priority);
            todoRepository.save(todo);
        }
    }

    // 10. 未完成数量统计
    @Override
    public long countUnfinished(Long userId) {
        return todoRepository.countByUserIdAndStatusAndIsDelete(userId, 0, 0);
    }

    // 11. 按优先级筛选
    @Override
    public List<Todo> getTodoListByUserAndPriority(Long userId, Integer priority) {
        return todoRepository.findByUserIdAndPriorityAndIsDelete(userId, priority, 0);
    }
}