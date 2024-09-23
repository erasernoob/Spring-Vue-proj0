package org.backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.entity.RestBean;
import org.backend.service.MyUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.Writer;

@Configuration
public class SecurityConfiguration {

    @Bean
    MyUserService service;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> {
                    conf.requestMatchers("/api/auth/**").permitAll();
                    conf.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(conf -> {
                    conf.loginProcessingUrl("/api/auth/login");
                    conf.successHandler(this::onAuthentication);
                    conf.failureHandler(this::onAuthentication);
                    conf.authenticationDetailsSource(service);
                    conf.
                })
                .logout(conf -> {
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessHandler(this::onAuthenticationLogout);
                })
                .build();
    }

    public void onAuthenticationLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        Writer writer = response.getWriter();
        writer.write(RestBean.success("退出成功", 200,  request.getUserPrincipal().getName()).toJson());
    }

    public void onAuthentication(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        Writer writer = response.getWriter();
        if(object instanceof Exception) {
            writer.write(RestBean.failure("登录失败", 400 , ((Exception) object).getMessage()).toJson());
        } else if( object instanceof Authentication) {
            writer.write(RestBean.success("登录成功", 200 , request.getUserPrincipal()).toJson());
        }
    }


}
