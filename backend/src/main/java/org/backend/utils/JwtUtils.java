package org.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}") // 将配置中的key直接注入
    public String value;

    public Date expiration;

    @Resource
    public StringRedisTemplate stringRedisTemplate;

    /**
     * 将token拉入黑名单
     *
     * 利用redis来设置，退出的业务逻辑
     * 将退出的token加入黑名单
     * Redis保存token：
     * 为了区分redis中所存储的key的值：添加key的前缀
     */
    public boolean inValidateJwt( String headToken) {
        String token = this.convertToken(headToken);
        if(token == null) {return false;}
        Algorithm algorithm = Algorithm.HMAC256(value);
        // 首先先对token有效性进行验证
        try {
            DecodedJWT jwt = JWT.decode(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        } catch (JWTDecodeException e) {
            // 解码出来的jwt无效，返回false
            return false;
        }
    }

    /**
     * 清除token
     * # 将redis中保存token的时间，设置为用户此时退出的时候，还剩下的有效的时间
     */
    public boolean deleteToken(String uuid, Date time) {
        // 如果token已经在黑名单中，直接返回false
        if(isInvalidKey(uuid)) {
            return false;
        }
        Date now = new Date();
        long expire =  Math.max(time.getTime() - now.getTime(), 0); // 防止用已经过期的token来，从而得到负数
        // 写入token（退出登录的），也就是把token拉进黑名单
        // 这里只用存key，用的是特定的uuid的和前缀的key，就可以判别是否存在该token
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        // 加入黑名单成功
        return true;
    }

    /**
     * 判断该token所对应的key是否已经存放在黑名单中了
     */
    public  Boolean isInvalidKey(String uuid) {
        return stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    /**
     * 在获取到token之后，用此工具类方法进行解析token
     * 1. 判断token是否有效
     * 2. 判断token是否过期
     */
    public  DecodedJWT resolveJwt(String token) {
        Algorithm algorithm = Algorithm.HMAC256("absdjfhlafjas"); // 设置密钥
        token = convertToken(token);
        if(token == null) return null; // 判断转换之后的token是否有效
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token);
            if(isInvalidKey(jwt.getId())) {return null;} // 如果token在黑名单中，直接返回空
            Date expiresAt = jwt.getExpiresAt();
            // 如果过期返回null，为过期返回正确有效的解析过后的token
            return new Date().after(expiresAt) ? null : jwt;  // 判断日期是否过期
        } catch (JWTVerificationException e) {
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



    public String convertToken(String headerToken) {
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
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("name", username)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expiration) //设定过期时间
                .withIssuedAt(new Date()) // 给定令牌颁发时间
                .sign(algorithm); // 签名算法
    }

    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }
    /**
     * 计算过期时间
     */
}
