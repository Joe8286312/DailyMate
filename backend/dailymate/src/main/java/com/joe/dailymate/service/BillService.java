// BillService.java
package com.joe.dailymate.service;

import com.joe.dailymate.entity.Bill;
import java.util.Date;
import java.util.List;

public interface BillService {
    Bill addBill(Bill bill);
    Bill updateBill(Bill bill);
    void deleteBill(Long id);
    Bill findById(Long id);
    List<Bill> getBillListByUserAndDate(Long userId, Date date);
    List<Bill> getBillListByUser(Long userId);
}