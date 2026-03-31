package com.joe.dailymate.service;

import com.joe.dailymate.entity.Bill;
import com.joe.dailymate.repository.BillRepository;
import com.joe.dailymate.service.impl.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * BillServiceImpl 单元测试
 * 使用 Mockito Mock Repository，不启动 Spring 容器，不连接数据库
 */
@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private Bill income500;
    private Bill expense200;
    private Bill expense100;

    @BeforeEach
    void setUp() {
        // 准备测试数据：2026-03 的三条账单
        income500 = new Bill();
        income500.setId(1L);
        income500.setUserId(1L);
        income500.setType(1);        // 收入
        income500.setAmount(500.0);
        income500.setCategory("工资");
        income500.setIsDelete(0);
        income500.setDate(Date.valueOf(LocalDate.of(2026, 3, 10)));

        expense200 = new Bill();
        expense200.setId(2L);
        expense200.setUserId(1L);
        expense200.setType(2);       // 支出
        expense200.setAmount(200.0);
        expense200.setCategory("餐饮");
        expense200.setIsDelete(0);
        expense200.setDate(Date.valueOf(LocalDate.of(2026, 3, 15)));

        expense100 = new Bill();
        expense100.setId(3L);
        expense100.setUserId(1L);
        expense100.setType(2);       // 支出
        expense100.setAmount(100.0);
        expense100.setCategory("餐饮");
        expense100.setIsDelete(0);
        expense100.setDate(Date.valueOf(LocalDate.of(2026, 3, 20)));
    }

    // ==================== statMonth ====================

    @Test
    @DisplayName("月统计：收入500，支出300，结余200")
    void statMonth_shouldCalcCorrectly() {
        // given：Mock Repository 返回三条账单
        when(billRepository.findByUserIdAndIsDeleteAndDateBetween(
                eq(1L), eq(0), any(), any()))
                .thenReturn(List.of(income500, expense200, expense100));

        // when
        Map<String, Double> result = billService.statMonth(1L, 2026, 3);

        // then
        assertThat(result.get("income")).isEqualTo(500.0);
        assertThat(result.get("expense")).isEqualTo(300.0);
        assertThat(result.get("total")).isEqualTo(200.0);
    }

    @Test
    @DisplayName("月统计：当月无账单，全部返回0")
    void statMonth_noData_shouldReturnZero() {
        when(billRepository.findByUserIdAndIsDeleteAndDateBetween(
                eq(1L), eq(0), any(), any()))
                .thenReturn(List.of());

        Map<String, Double> result = billService.statMonth(1L, 2026, 2);

        assertThat(result.get("income")).isEqualTo(0.0);
        assertThat(result.get("expense")).isEqualTo(0.0);
        assertThat(result.get("total")).isEqualTo(0.0);
    }

    // ==================== statTrend ====================

    @Test
    @DisplayName("趋势统计：返回结果条数等于请求天数")
    void statTrend_resultSizeShouldEqualDays() {
        // given：只有今天有账单，其余天为空
        when(billRepository.findByUserIdAndIsDeleteAndDateBetween(
                eq(1L), eq(0), any(), any()))
                .thenReturn(List.of(income500));

        // when：请求7天趋势
        List<Map<String, Object>> result = billService.statTrend(1L, 7);

        // then：返回7条（每天一条，空天也占位）
        assertThat(result).hasSize(7);
    }

    @Test
    @DisplayName("趋势统计：结果按日期升序排列")
    void statTrend_shouldBeOrderedByDateAsc() {
        when(billRepository.findByUserIdAndIsDeleteAndDateBetween(
                eq(1L), eq(0), any(), any()))
                .thenReturn(List.of());

        List<Map<String, Object>> result = billService.statTrend(1L, 3);

        // 第一条日期 < 第二条日期 < 第三条日期
        LocalDate d0 = LocalDate.parse((String) result.get(0).get("date"));
        LocalDate d1 = LocalDate.parse((String) result.get(1).get("date"));
        LocalDate d2 = LocalDate.parse((String) result.get(2).get("date"));
        assertThat(d0).isBefore(d1);
        assertThat(d1).isBefore(d2);
    }

    @Test
    @DisplayName("趋势统计：每条结果都包含 date/income/expense/total 四个字段")
    void statTrend_eachDayMapShouldContainRequiredKeys() {
        when(billRepository.findByUserIdAndIsDeleteAndDateBetween(
                eq(1L), eq(0), any(), any()))
                .thenReturn(List.of());

        List<Map<String, Object>> result = billService.statTrend(1L, 3);

        result.forEach(day -> {
            assertThat(day).containsKeys("date", "income", "expense", "total");
        });
    }

    // ==================== batchDelete ====================

    @Test
    @DisplayName("批量软删除：所有账单的 isDelete 被置为1")
    void batchDelete_shouldMarkAllAsDeleted() {
        // given
        when(billRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(income500, expense200));

        // when
        billService.batchDelete(List.of(1L, 2L));

        // then：saveAll 被调用，且两条记录都被标记为删除
        verify(billRepository).saveAll(argThat(bills -> {
            List<Bill> list = (List<Bill>) bills;
            return list.stream().allMatch(b -> b.getIsDelete() == 1);
        }));
    }

    @Test
    @DisplayName("批量软删除：已删除的账单不会被重复处理")
    void batchDelete_shouldSkipAlreadyDeleted() {
        Bill alreadyDeleted = new Bill();
        alreadyDeleted.setId(4L);
        alreadyDeleted.setIsDelete(1); // 已软删除

        when(billRepository.findAllById(List.of(4L)))
                .thenReturn(List.of(alreadyDeleted));

        billService.batchDelete(List.of(4L));

        // isDelete 仍然是1，没有被改动
        verify(billRepository).saveAll(argThat(bills -> {
            List<Bill> list = (List<Bill>) bills;
            return list.get(0).getIsDelete() == 1;
        }));
    }

    // ==================== addBill / deleteBill ====================

    @Test
    @DisplayName("addBill：自动设置 isDelete=0")
    void addBill_shouldSetIsDeleteToZero() {
        Bill newBill = new Bill();
        newBill.setUserId(1L);
        newBill.setAmount(100.0);
        when(billRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bill saved = billService.addBill(newBill);

        assertThat(saved.getIsDelete()).isEqualTo(0);
    }

    @Test
    @DisplayName("deleteBill：软删除将 isDelete 置为1")
    void deleteBill_shouldSetIsDeleteToOne() {
        when(billRepository.findById(1L)).thenReturn(Optional.of(income500));
        when(billRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        billService.deleteBill(1L);

        assertThat(income500.getIsDelete()).isEqualTo(1);
        verify(billRepository).save(income500);
    }

    @Test
    @DisplayName("deleteBill：id不存在时不抛异常，静默忽略")
    void deleteBill_notFound_shouldDoNothing() {
        when(billRepository.findById(99L)).thenReturn(Optional.empty());

        // 不抛异常
        billService.deleteBill(99L);

        verify(billRepository, never()).save(any());
    }
}
