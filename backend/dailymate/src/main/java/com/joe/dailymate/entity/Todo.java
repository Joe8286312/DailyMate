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

    // ================== 新增内容："创建/更新时间自动管理" ===================
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt; // 最后更新时间

    // ================== 新增字段："开始/结束/完成时间" ===================
    /**
     * 计划/实际开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 计划/实际截止时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 实际结束/完成时间
     * status==1时建议赋值
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finish_time")
    private Date finishTime;

    // ================== 自动填充时间戳逻辑（无需手动赋值） ===================
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}