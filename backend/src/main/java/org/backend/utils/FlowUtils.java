package org.backend.utils;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 对redis请求的 xianliu 工具类
 */
@Component
public class FlowUtils {

    @Resource
    StringRedisTemplate template;

    public boolean limitOnceCheck(String key, int blockTime) {
        if(Boolean.TRUE.equals(template.hasKey(key))) { //
            // 现在仍在冷却时间当中
            return false;
        } else {
            // redis 中加入冷却时间
            template.opsForValue().set(key, "",  blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
