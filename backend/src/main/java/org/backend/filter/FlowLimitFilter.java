package org.backend.filter;


import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.entity.RestBean;
import org.backend.utils.Const;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 限制流量的操作
 */
@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if(tryCount(ip)) { //
            System.out.println("before");
            chain.doFilter(request, response);
        } else {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(RestBean.forBidden().toJson());
        }
    }

    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT + ip))) {
                // 表示已经被封锁了
                return false;
            } else {
                return limitPeriodCheck(ip);
            }
        }
    }

    private boolean limitPeriodCheck(String ip) {
        System.out.println("in check");
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_COUNTER + ip))) {
            long l = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(Const.FLOW_COUNTER + ip)).orElse(0L);
            if(l > 10) {
                stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT + ip, "", 30, TimeUnit.SECONDS); // 直接封禁三十秒
                return false;
            }
        }
         else{
            System.out.println("in and set value");
            stringRedisTemplate.opsForValue().set(Const.FLOW_COUNTER + ip, "1", 10, TimeUnit.SECONDS);
        }
        return true;
    }
















}
