package com.joe.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "bill")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer type; // 1-收入, 2-支出
    private String category;
    private Double amount;
    private String remark;

    @Temporal(TemporalType.DATE)
    private Date date;

    // 新增字段：软删除标记
    @Column(name = "is_delete")
    private Integer isDelete = 0; // 0未删除，1已删除

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}