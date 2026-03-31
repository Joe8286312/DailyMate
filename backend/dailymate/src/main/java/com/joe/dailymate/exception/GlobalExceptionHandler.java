package com.joe.dailymate.exception;

import com.joe.dailymate.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：统一捕获异常，转换为标准 Result 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：越权 / 资源不存在 / 未登录等可预期错误
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 兜底：未预期的系统异常，返回 500，隐藏内部细节
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail(500, "服务器内部错误");
    }
}
