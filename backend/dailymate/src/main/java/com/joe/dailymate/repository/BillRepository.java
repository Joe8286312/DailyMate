package com.joe.dailymate.repository;

import com.joe.dailymate.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    // 新增：只查未删除
    List<Bill> findByUserIdAndIsDelete(Long userId, Integer isDelete);
    List<Bill> findByUserIdAndDateAndIsDelete(Long userId, Date date, Integer isDelete);
}