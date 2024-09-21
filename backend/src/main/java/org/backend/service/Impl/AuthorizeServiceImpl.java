package org.backend.service.Impl;

import jakarta.annotation.Resource;
import org.backend.entity.Account;
import org.backend.mapper.UserMapper;
import org.backend.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service // 作为自定义用户密码以及信息，校验用户登录的service类 实现自UserDetailService
public class AuthorizeServiceImpl implements AuthorizeService {


    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender; // 发送邮件的接口

    @Resource
    StringRedisTemplate stringRedisTemplate;

    // 重写验证服务方法
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.isEmpty()) {
            throw new UsernameNotFoundException("用户名不能为空！！");
        }
        Account account = mapper.findAccountByNameOrEmail(username);
        if(account == null) {
            throw new UsernameNotFoundException("用户名或密码错误。");
        }
        return User
                .withUsername(account.getUsername() )
                .password((account.getPassword()))
                .roles("admin")
                .build();
    }

    /**
     * 思路：1.先生成对应的验证码
     *  2.把邮箱地址和对应的验证码放进Redis中，发送验证码到指定的邮箱中
     *  4.用户在发出注册时，再取出Redis中的对应的键值对，查看验证码是否一致
     */

    @Override
    public boolean sendValidateEmail(String email, String sessionId) {
        String key = "email:" + sessionId + ":" + email;

        // 实现每间隔一段时间（一分钟）才能进行下一次请求的（当剩余时间剩余2分钟以下，就可以重新发, 重复）
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if(expire > 120)  return false;
        }
        Random random = new Random();
        // 随机生成并创建验证码
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(random.nextInt(10));
        }
        String code = builder.toString();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Mclaren平台验证码");
        message.setFrom(from);
        message.setTo(email);
        message.setText("验证码:" + code);
        try {
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(key, code, 3, TimeUnit.MINUTES); // redis中设置有效期为三分钟
            return true;
        } catch (MailSendException e) {
            e.printStackTrace();
            return false;
        }
    }
}
