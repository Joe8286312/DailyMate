package com.joe.dailymate.repository;

import com.joe.dailymate.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 基本功能
    List<Todo> findByUserIdAndIsDelete(Long userId, Integer isDelete);
    List<Todo> findByUserIdAndDateAndIsDelete(Long userId, Date date, Integer isDelete);

    // 分页查询
    Page<Todo> findByUserIdAndIsDelete(Long userId, Integer isDelete, Pageable pageable);

    // 新增功能支持
    List<Todo> findByUserIdAndStatusAndIsDelete(Long userId, Integer status, Integer isDelete);
    List<Todo> findByUserIdAndPriorityAndIsDelete(Long userId, Integer priority, Integer isDelete);
    long countByUserIdAndStatusAndIsDelete(Long userId, Integer status, Integer isDelete);

    // 日期区间
    List<Todo> findByUserIdAndIsDeleteAndDateBetween(Long userId, Integer isDelete, Date start, Date end);

    // 模糊搜索
    List<Todo> findByUserIdAndIsDeleteAndTitleContainingOrContentContaining(Long userId, Integer isDelete, String title, String content);
}