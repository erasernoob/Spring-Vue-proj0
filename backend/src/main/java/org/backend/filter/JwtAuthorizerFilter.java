package org.backend.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.backend.utils.JwtUtils;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizerFilter extends OncePerRequestFilter {
    @Resource
    public JwtUtils utils;


    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // 得到请求头
        DecodedJWT decodedJWT = utils.resolveJwt(authHeader);
        // 如果获取到了正确的token就开始解析用户的详细信息， 最后继续过滤
        if(decodedJWT != null) {
            UserDetails user = utils.toUserDetails(decodedJWT);
            // Security用来封装用户信息和权限的一个token类，构造函数分别要提供的是， 用户信息，凭证，用户的身份信息和权限
            // 之后继续的向其中添加更多的验证信息， 最后直接将该用户的验证信息直接丢入security上下文之后，就是相当于得到身份验证了
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 继续添加请求的认真信息
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 最后将认证信息直接丢进security的上下文中
            // 出于方便，可以在之后获取用户的ID
            request.setAttribute("id", utils.toId(decodedJWT));
        }
        filterChain.doFilter(request, response); // 继续下一个过滤
    }
}
