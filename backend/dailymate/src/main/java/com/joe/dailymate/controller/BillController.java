package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.exception.BusinessException;
import com.joe.dailymate.service.BillService;
import com.joe.dailymate.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Object getBillList(HttpServletRequest request,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        // 按日期精确查询时不分页（当天账单条数有限）
        if (date != null) {
            return billService.getBillListByUserAndDate(userId, date);
        }
        // 有 page 参数时返回分页结果，否则返回全量（兼容旧调用）
        if (page != null) {
            PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date", "id"));
            return billService.getBillListByUser(userId, pageable);
        }
        return billService.getBillListByUser(userId);
    }

    @GetMapping("/{id}")
    public Bill findById(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Bill bill = billService.findById(id);
        if (bill == null) throw BusinessException.notFound("账单不存在");
        if (!bill.getUserId().equals(currentUserId)) throw BusinessException.forbidden("无权访问该账单");
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
        if (bill == null) throw BusinessException.notFound("账单不存在");
        if (!bill.getUserId().equals(currentUserId)) throw BusinessException.forbidden("无权删除该账单");
        billService.deleteBill(id);
    }

    @PutMapping("/update")
    public Bill updateBill(@RequestBody Bill bill, HttpServletRequest request) {
        Long currentUserId = JwtUtil.getUserIdFromRequest(request);
        Bill existing = billService.findById(bill.getId());
        if (existing == null) throw BusinessException.notFound("账单不存在");
        if (!existing.getUserId().equals(currentUserId)) throw BusinessException.forbidden("无权修改该账单");
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
        if (bill == null) throw BusinessException.notFound("账单不存在");
        if (!bill.getUserId().equals(currentUserId)) throw BusinessException.forbidden("无权删除该账单");
        billService.hardDelete(id);
        return "OK";
    }
}