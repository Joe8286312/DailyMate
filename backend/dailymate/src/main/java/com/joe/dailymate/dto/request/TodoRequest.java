package com.joe.dailymate.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * 待办事项请求 DTO
 */
@Data
public class TodoRequest {

    private Long id;

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过 100 个字符")
    private String title;

    @Size(max = 500, message = "内容长度不能超过 500 个字符")
    private String content;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private Date startTime;

    @NotNull(message = "截止时间不能为空")
    private Date endTime;

    private Date finishTime;
}
