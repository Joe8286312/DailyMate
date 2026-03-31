package com.joe.dailymate.dto;

import lombok.Data;

/**
 * 统一接口响应体
 * 所有接口返回格式：{ "code": 200, "msg": "ok", "data": ... }
 */
@Data
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "ok", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "ok", null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
