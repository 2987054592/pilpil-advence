package com.pilpil.comment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private T data;
    private String message;
    private Integer code;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(200);
        return result;
    }
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setData(null);
        result.setCode(200);
        return result;
    }
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setMessage(message);
        result.setCode(500);
        return result;
    }
}
