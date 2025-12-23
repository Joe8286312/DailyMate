package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bill")
public class BillController {
    @Autowired
    private BillService billService;

    @GetMapping("/list")
    public List<Bill> getBillList(@RequestParam Long userId,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        if (date != null) {
            return billService.getBillListByUserAndDate(userId, date);
        } else {
            return billService.getBillListByUser(userId);
        }
    }

    @GetMapping("/{id}")
    public Bill findById(@PathVariable Long id) {
        return billService.findById(id);
    }

    @PostMapping("/add")
    public Bill addBill(@RequestBody Bill bill) {
        return billService.addBill(bill);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
    }

    @PutMapping("/update")
    public Bill updateBill(@RequestBody Bill bill) {
        return billService.updateBill(bill);
    }

    // ===== 新增业务接口 =====

    /**
     * 按日期范围过滤
     */
    @GetMapping("/range")
    public List<Bill> getBillsInRange(@RequestParam Long userId,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billService.getBillListByUserAndDateRange(userId, start, end);
    }

    /**
     * 按类型过滤
     */
    @GetMapping("/by-type")
    public List<Bill> getBillsByType(@RequestParam Long userId,
                                     @RequestParam Integer type,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billService.getBillListByUserAndType(userId, type, start, end);
    }

    /**
     * 按分类过滤
     */
    @GetMapping("/by-category")
    public List<Bill> getBillsByCategory(@RequestParam Long userId,
                                         @RequestParam String category,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billService.getBillListByUserAndCategory(userId, category, start, end);
    }

    /**
     * 按金额区间过滤
     */
    @GetMapping("/by-amount-range")
    public List<Bill> getBillsByAmountRange(@RequestParam Long userId,
                                            @RequestParam Double min,
                                            @RequestParam Double max,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billService.getBillListByUserAndAmountBetween(userId, min, max, start, end);
    }

    /**
     * 备注关键字模糊查询
     */
    @GetMapping("/search")
    public List<Bill> searchBill(@RequestParam Long userId, @RequestParam String keyword) {
        return billService.searchBill(userId, keyword);
    }

    /**
     * 月度收支统计
     */
    @GetMapping("/stat/month")
    public Map<String, Double> statMonth(@RequestParam Long userId,
                                         @RequestParam Integer year,
                                         @RequestParam Integer month) {
        return billService.statMonth(userId, year, month);
    }

    /**
     * 分类统计（如 pie 图）
     */
    @GetMapping("/stat/category")
    public Map<String, Double> statCategory(@RequestParam Long userId,
                                            @RequestParam Integer type,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billService.statCategory(userId, type, start, end);
    }

    /**
     * 近N天收支趋势
     */
    @GetMapping("/stat/trend")
    public List<Map<String, Object>> statTrend(@RequestParam Long userId, @RequestParam Integer days) {
        return billService.statTrend(userId, days);
    }

    /**
     * 批量软删除
     */
    @PutMapping("/batch/delete")
    public String batchDelete(@RequestBody Map<String, Object> req) {
        List<Integer> ids = (List<Integer>) req.get("ids");
        billService.batchDelete(ids.stream().map(Long::valueOf).toList());
        return "OK";
    }

    /**
     * 批量彻底物理删除
     */
    @PutMapping("/batch/hard-delete")
    public String batchHardDelete(@RequestBody Map<String, Object> req) {
        List<Integer> ids = (List<Integer>) req.get("ids");
        billService.batchHardDelete(ids.stream().map(Long::valueOf).toList());
        return "OK";
    }

    /**
     * 恢复软删除
     */
    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        billService.restore(id);
        return "OK";
    }

    /**
     * 彻底物理删除
     */
    @DeleteMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id) {
        billService.hardDelete(id);
        return "OK";
    }
}