package com.joe.dailymate.repository;

import com.joe.dailymate.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 新增：只查未删除
    List<Todo> findByUserIdAndIsDelete(Long userId, Integer isDelete);
    List<Todo> findByUserIdAndDateAndIsDelete(Long userId, Date date, Integer isDelete);
}