package org.backend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.entity.RestBean;
import org.backend.filter.JwtAuthorizerFilter;
import org.backend.service.MyUserService;
import org.backend.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;

@Configuration
public class SecurityConfiguration {


    @Resource
    JwtUtils utils;

    @Resource
    JwtAuthorizerFilter jwtAuthorizerFilter;


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
                })
                .sessionManagement(conf -> {
                    conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(new JwtAuthorizerFilter(), UsernamePasswordAuthenticationFilter.class) // 添加自定义的filter到配置中
                .exceptionHandling(conf -> {
                    conf.accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            response.setContentType("application/json; charset=utf-8");
                            response.getWriter().write("无法访问");
                        }
                    });
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
        writer.write(RestBean.success("退出成功", 200,  null).toJson());
    }

    public void onAuthentication(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        Writer writer = response.getWriter();
        if(object instanceof Exception) {
            writer.write(RestBean.failure("登录失败", 400 , ((Exception) object).getMessage()).toJson());
        } else if( object instanceof Authentication) {
            User user = (User)  ((Authentication) object).getPrincipal(); // 直接从认证类中将User的详细信息读取出来，便于JWt创建发放令牌使用
            String token = utils.createJwt(user, "baiyiheng", 1);
            writer.write(RestBean.success("登录成功", 200 , token).toJson());
        }
    }

//    @Bean
//    DataSource configureDataSource() {
//    }

}
