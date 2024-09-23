package org.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {



    @Value("${spring.security.jwt.key}") // 将配置中的key直接注入
    String value;

    /**
     * 创建令牌,令牌包括用户的详细信息
     * 利用authentication读取用户的信息创建独一无二的令牌
     * */
    public String createJwt(UserDetails userDetails, String username, int id) {
        System.out.println(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()); // 测试用
        Algorithm algorithm = Algorithm.HMAC256(value); // 设置密钥
        return JWT.create() .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(Granted
               Authority::getAuthority).toList())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) //设定过期时间
                .withIssuedAt(new Date()) // 给定令牌颁发时间
                .sign(algorithm); // 签名算法
    }

    /**
     * 计算过期时间
     */
}
