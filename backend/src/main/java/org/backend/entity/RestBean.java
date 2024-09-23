package org.backend.entity;

import com.alibaba.fastjson2.JSONObject;

public record RestBean<T>(String message, int code, T data) {
    public static <T> RestBean<T> success() {
        return new RestBean<>("请求成功", 200, null);
    }
    public static <T> RestBean<T> success(String messgae, int code, T data) {
        return new RestBean<>(messgae, code, data);
    }

    public static <T> RestBean<T> failure() {
        return new RestBean<>("请求失败", 400, null);
    }

    public static <T> RestBean<T> failure(String message , int code, T data) {
        return new RestBean<>(message, code, data);
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
