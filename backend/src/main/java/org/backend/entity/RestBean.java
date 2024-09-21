package org.backend.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;

@Data
public class  RestBean<T>{
    String message; T data; int code; boolean success;

    //        this.message = message;
//        this.data = data;
//        this.code = code;
//        this.success = false;;
//    }

    private RestBean(String message,  T data, int code,  Boolean success) {
        this.message = message;
        this.success = success;
        this.code = code;
        this.data = data;
    }

    public static <T> RestBean<T> success(T data){
        return new RestBean<T>("登录成功！", data, 200, true);
    }

    public static <T> RestBean<T> success(String message, int code){
        return new RestBean<T>(message, null, code, true);
    }

    public static <T> RestBean<T> failure(String message, int code, String exceptionMessage){
        return new RestBean<T>(message, null, code, false);
    }

    public String toJsonString() {
        return JSONObject
                .from(this, JSONWriter.Feature.WriteNulls)
                .toJSONString();
    }
}
