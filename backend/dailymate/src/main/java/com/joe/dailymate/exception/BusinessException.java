package com.joe.dailymate.exception;

/**
 * 业务异常：可预期的错误（越权、资源不存在、参数非法等）
 * 与 RuntimeException 区分，方便全局处理器精确捕获
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // 常用快捷方法
    public static BusinessException notFound(String msg) {
        return new BusinessException(404, msg);
    }

    public static BusinessException forbidden(String msg) {
        return new BusinessException(403, msg);
    }

    public static BusinessException unauthorized(String msg) {
        return new BusinessException(401, msg);
    }
}
