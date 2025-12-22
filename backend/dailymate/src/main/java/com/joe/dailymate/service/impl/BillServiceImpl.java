package com.joe.dailymate.service.impl;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.repository.BillRepository;
import com.joe.dailymate.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

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
}