package org.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}") // 将配置中的key直接注入
    String value;

    public Date expiration;

    /**
     * 在获取到token之后，用此工具类方法进行解析token
     * 1. 判断token是否有效
     * 2. 判断token是否过期
     */
    public  DecodedJWT resolveJwt(String token) {
        Algorithm algorithm = Algorithm.HMAC256("absdjfhlafjas"); // 设置密钥
        token = convertToken(token);
        System.out.println("After Convert Token: " + token);
        if(token == null) return null; // 判断转换之后的token是否有效
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("jwt" + jwt);
            Date expiresAt = jwt.getExpiresAt();
            System.out.println("expiresAt" + expiresAt);
            // 如果过期返回null，为过期返回正确有效的解析过后的token
            return new Date().after(expiresAt) ? null : jwt;  // 判断日期是否过期
        } catch (JWTVerificationException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * 根据token解析成user的详细信息的
     */
    public UserDetails toUserDetails(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString()) // 将解析得到的详细信息封装成用户进行返回
                .password("*******") // 因为在token中并没有存密码？？
                .authorities(claims.get("authorities").asArray(String.class)) // 这支中包含了用户的权限和角色等
                .build();
    }



    private String convertToken(String headerToken) {
        if (headerToken == null || !headerToken.startsWith("Bearer ")) {
            return null;
        }
        return headerToken.split(" ")[1]; // 请求头中所带token前面有前缀"Bear " 在这里给拼接掉
    }

    /**
     * 创建令牌,令牌包括用户的详细信息
     * 利用authentication读取用户的信息创建独一无二的令牌
     * */
    public String createJwt(UserDetails userDetails, String username, int id) {
        System.out.println(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()); // 测试用
        Algorithm algorithm = Algorithm.HMAC256("absdjfhlafjas"); // 设置密钥
        expiration =  new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        return JWT.create()
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expiration) //设定过期时间
                .withIssuedAt(new Date()) // 给定令牌颁发时间
                .sign(algorithm); // 签名算法
    }
    /**
     * 计算过期时间
     */
}
