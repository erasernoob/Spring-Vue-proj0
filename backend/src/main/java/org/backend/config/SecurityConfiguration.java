package org.backend.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.backend.entity.RestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.Writer;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Resource
    HttpSecurity http;

    @SneakyThrows
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> {
                    conf.anyRequest().authenticated(); // 最初的一个设置，访问页面需要认证
                })
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin( e -> {
                    e.loginProcessingUrl("/api/auth/login"); // 加入登录页面的接口
                    e.successHandler(this::onAuthenticationSuccess); // 自定义成功和失败之后的handler和返回的信息，不至于失败或成功之后Springsecurity自动重定向跳转
                    e.failureHandler(this::onAuthenticationFailure);
                })
                .logout(e-> {
                    e.logoutUrl("/api/auth/logout"); // 接入退出登录的接口
                })
                .exceptionHandling( e -> {
                    e.authenticationEntryPoint(this::onAuthenticationFailure); // 加入没有进行验证，但是要访问除login之外的页面的处理，而不是直接拒绝访问302
                })
                .build();
    }

    private void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
        writer.write(JSONObject.toJSONString((RestBean.failure(e.getMessage(), 401, e.getMessage()))));
    }


    // 成功需要直接返回JSON数据格式
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
        writer.write(RestBean.success(authentication.getName()).toJsonString());
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
        writer.write(JSONObject.toJSONString((RestBean.failure(exception.getMessage(), 401, exception.getMessage()))));
    }





}
