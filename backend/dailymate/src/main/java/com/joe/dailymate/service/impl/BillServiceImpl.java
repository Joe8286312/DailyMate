package com.joe.dailymate.service.impl;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.repository.BillRepository;
import com.joe.dailymate.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {
    @Autowired
    private BillRepository billRepository;

    @Override
    public Bill addBill(Bill bill) {
        bill.setIsDelete(0);
        return billRepository.save(bill);
    }

    @Override
    public Bill updateBill(Bill bill) {
        return billRepository.save(bill);
    }

    // 软删除替换硬删除
    @Override
    public void deleteBill(Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null && bill.getIsDelete() != null && bill.getIsDelete() == 0) {
            bill.setIsDelete(1);
            billRepository.save(bill);
        }
        // billRepository.deleteById(id); // 被软删除替代
    }

    @Override
    public Bill findById(Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null && bill.getIsDelete() != null && bill.getIsDelete() == 0) {
            return bill;
        }
        return null;
    }

    @Override
    public List<Bill> getBillListByUserAndDate(Long userId, Date date) {
        return billRepository.findByUserIdAndDateAndIsDelete(userId, date, 0);
    }

    @Override
    public List<Bill> getBillListByUser(Long userId) {
        return billRepository.findByUserIdAndIsDelete(userId, 0);
    }

    // ========= 新增功能实现 ==========

    // 日期范围筛选
    @Override
    public List<Bill> getBillListByUserAndDateRange(Long userId, Date start, Date end) {
        return billRepository.findByUserIdAndIsDeleteAndDateBetween(userId, 0, start, end);
    }

    // 类型（收支）筛选
    @Override
    public List<Bill> getBillListByUserAndType(Long userId, Integer type, Date start, Date end) {
        if (type == 0) return getBillListByUserAndDateRange(userId, start, end); // 0-全部
        return billRepository.findByUserIdAndTypeAndIsDeleteAndDateBetween(userId, type, 0, start, end);
    }

    // 分类筛选
    @Override
    public List<Bill> getBillListByUserAndCategory(Long userId, String category, Date start, Date end) {
        return billRepository.findByUserIdAndCategoryAndIsDeleteAndDateBetween(userId, category, 0, start, end);
    }

    // 金额区间筛选
    @Override
    public List<Bill> getBillListByUserAndAmountBetween(Long userId, Double minAmount, Double maxAmount, Date start, Date end) {
        return billRepository.findByUserIdAndIsDeleteAndAmountBetweenAndDateBetween(userId, 0, minAmount, maxAmount, start, end);
    }

    // 模糊备注查找
    @Override
    public List<Bill> searchBill(Long userId, String keyword) {
        return billRepository.findByUserIdAndIsDeleteAndRemarkContaining(userId, 0, keyword);
    }

    /**
     * 月收支统计
     * @return Map key: income, expense, total
     */
    @Override
    public Map<String, Double> statMonth(Long userId, Integer year, Integer month) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last = first.withDayOfMonth(first.lengthOfMonth());
        Date from = Date.from(first.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(last.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Bill> list = getBillListByUserAndDateRange(userId, from, to);
        double income = list.stream().filter(b -> b.getType() == 1).mapToDouble(Bill::getAmount).sum();
        double expense = list.stream().filter(b -> b.getType() == 2).mapToDouble(Bill::getAmount).sum();
        Map<String, Double> stat = new HashMap<>();
        stat.put("income", income);
        stat.put("expense", expense);
        stat.put("total", income - expense);
        return stat;
    }

    /**
     * 分类统计（如饼图）
     * @return Map<分类, 金额合计>
     */
    @Override
    public Map<String, Double> statCategory(Long userId, Integer type, Date start, Date end) {
        List<Bill> list = getBillListByUserAndType(userId, type, start, end);
        return list.stream().collect(Collectors.groupingBy(
                Bill::getCategory,
                Collectors.summingDouble(Bill::getAmount)
        ));
    }

    /**
     * N天趋势，返回每日 income expense total
     */
    @Override
    public List<Map<String, Object>> statTrend(Long userId, Integer days) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> res = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Date from = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date to = Date.from(day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Bill> list = getBillListByUserAndDateRange(userId, from, to);
            double income = list.stream().filter(b -> b.getType() == 1).mapToDouble(Bill::getAmount).sum();
            double expense = list.stream().filter(b -> b.getType() == 2).mapToDouble(Bill::getAmount).sum();
            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", day.toString());
            dayStat.put("income", income);
            dayStat.put("expense", expense);
            dayStat.put("total", income - expense);
            res.add(dayStat);
        }
        return res;
    }

    // 批量软删除
    @Override
    public void batchDelete(List<Long> ids) {
        List<Bill> list = billRepository.findAllById(ids);
        for (Bill bill : list) {
            if (bill.getIsDelete() != null && bill.getIsDelete() == 0) {
                bill.setIsDelete(1);
            }
        }
        billRepository.saveAll(list);
    }

    // 彻底删除
    @Override
    public void hardDelete(Long id) {
        billRepository.deleteById(id);
    }

    // 批量彻底删除
    @Override
    public void batchHardDelete(List<Long> ids) {
        billRepository.deleteAllById(ids);
    }

    // 恢复账单
    @Override
    public void restore(Long id) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill != null && bill.getIsDelete() != null && bill.getIsDelete() == 1) {
            bill.setIsDelete(0);
            billRepository.save(bill);
        }
    }
}