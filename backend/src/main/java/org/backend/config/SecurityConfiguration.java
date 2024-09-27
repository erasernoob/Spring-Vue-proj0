package org.backend.config;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.entity.RestBean;
import org.backend.entity.dto.AccountDto;
import org.backend.entity.vo.response.AuthorizeVO;
import org.backend.filter.JwtAuthorizerFilter;
import org.backend.service.AccountService;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.Writer;

@Configuration
public class SecurityConfiguration {

    @Resource
    public JwtAuthorizerFilter jwtAuthorizerFilter;

    @Resource
    public JwtUtils utils;

    @Resource
    AccountService accountService;


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
                .addFilterBefore(jwtAuthorizerFilter, UsernamePasswordAuthenticationFilter.class) // 添加自定义的filter到配置中
                .exceptionHandling(conf -> {
                    conf.authenticationEntryPoint(new AuthenticationEntryPoint() {
                        // 处理没有登录验证的情况
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                            response.setContentType("application/json; charset=utf-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            Writer writer = response.getWriter();
                            writer.write(RestBean.failure("未登录,拒绝访问", 0, null).toJson());
                        }
                    });
                    // 处理已经登录，但是权限所导致的无法进入的情况
                    conf.accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                            response.setContentType("application/json; charset=utf-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            // FORBIDDEN
                            response.getWriter().write(RestBean.failure(accessDeniedException.getMessage(), 403, null).toJson());
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
        // 拉黑token
        boolean result = utils.inValidateJwt(request.getHeader("Authorization")); // 获取到验证信息
        Writer writer = response.getWriter();
        if(result) {
            writer.write(RestBean.success("退出成功", 200,  null).toJson());
        } else {
            writer.write(RestBean.success("退出失败", 400,  null).toJson());

        }

    }

    public void onAuthentication(HttpServletRequest request, HttpServletResponse response, Object object) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        Writer writer = response.getWriter();
        if(object instanceof Exception) {
            writer.write(RestBean.failure("登录失败", 400 , ((Exception) object).getMessage()).toJson());
        } else if( object instanceof Authentication) {
            User user = (User)  ((Authentication) object).getPrincipal(); // 直接从认证类中将User的详细信息读取出来，便于JWt创建发放令牌使用
            AccountDto accountDto = accountService.findAccountByNameOrEmail(user.getUsername());
            String token = utils.createJwt(user, accountDto.getUsername(), accountDto.getId());
            AuthorizeVO authorizeVO = accountDto.asViewObject(AuthorizeVO.class, v -> {
                v.setToken(token);
                v.setExpire(utils.expiration.toString());
            });
            writer.write(RestBean.success("登录成功", 200 , authorizeVO).toJson());
        }
    }
}
