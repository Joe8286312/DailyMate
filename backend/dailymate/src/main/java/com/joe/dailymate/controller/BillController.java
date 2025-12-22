package com.joe.dailymate.controller;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bill")
public class BillController {
    @Autowired
    private BillService billService;

//    @GetMapping("/list")
//    public List<Bill> getBillList(@RequestParam Long userId, @RequestParam(required = false) Date date) {
//        if (date != null) {
//            return billService.getBillListByUserAndDate(userId, date);
//        } else {
//            return billService.getBillListByUser(userId);
//        }
//    }


//    @GetMapping("/list")
//    public List<Todo> getTodoList(@RequestParam Long userId,
//                                  @RequestParam(required = false)
//                                  @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
//        if (date != null) {
//            return todoService.getTodoListByUserAndDate(userId, date);
//        } else {
//            return todoService.getTodoListByUser(userId);
//        }
//    }
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
}