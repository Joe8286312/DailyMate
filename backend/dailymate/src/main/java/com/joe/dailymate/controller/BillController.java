package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.service.BillService;
import com.joe.dailymate.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    public List<Bill> getBillList(HttpServletRequest request,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        if (date != null) {
            return billService.getBillListByUserAndDate(userId, date);
        } else {
            return billService.getBillListByUser(userId);
        }
    }

    @GetMapping("/{id}")
    public Bill findById(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Bill bill = billService.findById(id);
        // 归属校验：不是自己的账单返回 null
        if (bill == null || !bill.getUserId().equals(currentUserId)) return null;
        return bill;
    }

    @PostMapping("/add")
    public Bill addBill(@RequestBody Bill bill, HttpServletRequest request) {
        // 强制用 token 里的 userId，忽略客户端传来的值
        bill.setUserId(JwtUtil.getUserIdFromRequest(request));
        return billService.addBill(bill);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBill(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Bill bill = billService.findById(id);
        if (bill != null && bill.getUserId().equals(currentUserId)) {
            billService.deleteBill(id);
        }
    }

    @PutMapping("/update")
    public Bill updateBill(@RequestBody Bill bill, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        // 先查原始记录，确认归属
        Bill existing = billService.findById(bill.getId());
        if (existing == null || !existing.getUserId().equals(currentUserId)) return null;
        // 防止客户端篡改 userId
        bill.setUserId(currentUserId);
        return billService.updateBill(bill);
    }

    // ===== 新增业务接口 =====

    @GetMapping("/range")
    public List<Bill> getBillsInRange(HttpServletRequest request,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.getBillListByUserAndDateRange(userId, start, end);
    }

    @GetMapping("/by-type")
    public List<Bill> getBillsByType(HttpServletRequest request,
                                     @RequestParam Integer type,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.getBillListByUserAndType(userId, type, start, end);
    }

    @GetMapping("/by-category")
    public List<Bill> getBillsByCategory(HttpServletRequest request,
                                         @RequestParam String category,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.getBillListByUserAndCategory(userId, category, start, end);
    }

    @GetMapping("/by-amount-range")
    public List<Bill> getBillsByAmountRange(HttpServletRequest request,
                                            @RequestParam Double min,
                                            @RequestParam Double max,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.getBillListByUserAndAmountBetween(userId, min, max, start, end);
    }

    @GetMapping("/search")
    public List<Bill> searchBill(HttpServletRequest request, @RequestParam String keyword) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.searchBill(userId, keyword);
    }

    @GetMapping("/stat/month")
    public Map<String, Double> statMonth(HttpServletRequest request,
                                         @RequestParam Integer year,
                                         @RequestParam Integer month) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.statMonth(userId, year, month);
    }

    @GetMapping("/stat/category")
    public Map<String, Double> statCategory(HttpServletRequest request,
                                            @RequestParam Integer type,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.statCategory(userId, type, start, end);
    }

    @GetMapping("/stat/trend")
    public List<Map<String, Object>> statTrend(HttpServletRequest request, @RequestParam Integer days) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return billService.statTrend(userId, days);
    }

    @PutMapping("/batch/delete")
    public String batchDelete(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        List<Integer> ids = (List<Integer>) req.get("ids");
        List<Long> ownedIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> {
                    Bill bill = billService.findById(id);
                    return bill != null && bill.getUserId().equals(currentUserId);
                })
                .toList();
        billService.batchDelete(ownedIds);
        return "OK";
    }

    @PutMapping("/batch/hard-delete")
    public String batchHardDelete(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        List<Integer> ids = (List<Integer>) req.get("ids");
        List<Long> ownedIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> {
                    Bill bill = billService.findById(id);
                    return bill != null && bill.getUserId().equals(currentUserId);
                })
                .toList();
        billService.batchHardDelete(ownedIds);
        return "OK";
    }

    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        billService.restore(id);
        return "OK";
    }

    @DeleteMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Bill bill = billService.findById(id);
        if (bill != null && bill.getUserId().equals(currentUserId)) {
            billService.hardDelete(id);
        }
        return "OK";
    }
}