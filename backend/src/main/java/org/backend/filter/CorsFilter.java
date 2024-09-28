package org.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.utils.Const;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**'
 * 操作跨域请求的过滤器
 * 需要进行限制流
 */
@Component
@Order(Const.CORS_ORDER) // 设置优先级，一般cors优先级最小
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(response, request);
        super.doFilter(request, response, chain);
    }

    private void addCorsHeader(HttpServletResponse response, HttpServletRequest request) {
        // origin是在请求头中携带的源地址
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin")); // 添加允许的头文件origin连接
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");


    }
}
