package com.joe.dailymate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "todo")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String content;
    private Integer priority;
    private Integer status;

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