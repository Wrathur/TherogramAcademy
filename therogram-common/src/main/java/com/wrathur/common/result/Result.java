package com.wrathur.common.result;

import lombok.Data;

import java.io.Serializable;

// 后端统一返回结果
@Data
public class Result<T> implements Serializable {

    private Integer code; // 状态码
    private String msg; // 错误信息
    private T data; // 数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.code = 200;
        result.data = object;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}