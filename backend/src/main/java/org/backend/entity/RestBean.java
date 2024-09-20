package org.backend.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(String message, T data, int code) {

    public RestBean(String message, T data, int code) {
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static <T> RestBean<T> success(T data){
        return new RestBean<T>("登录成功！", data, 200);
    }

    public static <T> RestBean<T> success(String message, int code){
        return new RestBean<T>(message, null, code);
    }

    public static <T> RestBean<T> failure(String message, int code, String exceptionMessage){
        return new RestBean<T>(message, null, code);
    }

    public String toJsonString() {
        return JSONObject
                .from(this, JSONWriter.Feature.WriteNulls)
                .toJSONString();
    }
}
