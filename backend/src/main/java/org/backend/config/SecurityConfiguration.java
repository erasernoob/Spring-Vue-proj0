package org.backend.config;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.backend.entity.RestBean;
import org.backend.service.AuthorizeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService; // 自定义的UsersDetails

    @Resource
    DataSource dataSource;

    @SneakyThrows
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> {
                    conf.requestMatchers("/api/auth/**").permitAll(); // 放行所有有关对与验证的接口
                    conf.anyRequest().authenticated(); // 最初的一个设置，访问页面需要认证
                })
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin( e -> {
                    e.loginProcessingUrl("/api/auth/login"); // 加入登录页面的接口
                    e.successHandler(this::onAuthenticationSuccess); // 自定义成功和失败之后的handler和返回的信息，不至于失败或成功之后Springsecurity自动重定向跳转
                    e.failureHandler(this::onAuthenticationFailure);
                })
                .rememberMe(e -> {
                    e.rememberMeParameter("remember"); // 修改掉默认的remember-me名字
                    e.tokenRepository(persistentTokenRepository()); // 将remember仓库加入到配置中
                    e.tokenValiditySeconds(3600 * 24 * 7);
                })
                .cors(e -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.addAllowedOriginPattern("*");
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.addExposedHeader("*"); //
                    corsConfiguration.addAllowedMethod("*");
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    e.configurationSource(source);

                })
                .logout(e-> {
                    e.logoutUrl("/api/auth/logout"); // 接入退出登录的接口
                    e.logoutSuccessHandler(this::onAuthenticationSuccess); // 这里照样需要给logout之后的自定义的handler
                })
                .userDetailsService(authorizeService) // 将自定义的用户验证的service类加入配置投入使用过程
                .exceptionHandling( e -> {
                    e.authenticationEntryPoint(this::onAuthenticationFailure); // 加入没有进行验证，但是要访问除login之外的页面的处理，而不是直接拒绝访问302
                })
                .build();
    }

    // 配置session
    @Bean
     PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource); // 设置当前的数据源
//        jdbcTokenRepository.setCreateTableOnStartup(Boolean.TRUE); // 在最开始第一次的时候打开创建表的功能（因为根本就没有表来建立）第一次打开后续必须关闭
        return jdbcTokenRepository;
    }

    private void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
    }

    /**
     * 将passwordEnCoder注册为Bean直接交给Security进行配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 成功需要直接返回JSON数据格式
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
        if(request.getRequestURI().endsWith("/logout")) {
            writer.write(RestBean.success("退出成功！", 200).toJsonString());
        } else  {
            writer.write((RestBean.success(authentication.getName())).toJsonString());
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        Writer writer = response.getWriter();
        writer.write(RestBean.failure(exception.getMessage(),400).toJsonString());
    }
}
