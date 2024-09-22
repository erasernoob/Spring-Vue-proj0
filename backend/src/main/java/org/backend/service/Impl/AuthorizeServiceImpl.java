package org.backend.service.Impl;

import ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;
import jakarta.annotation.Resource;
import org.backend.entity.Account;
import org.backend.entity.RestBean;
import org.backend.mapper.UserMapper;
import org.backend.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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


    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


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
                .withUsername(account.getUsername())
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
    public String sendValidateEmailForRegister(String email, String sessionId) {
        String key = "email:" + sessionId + ":" + email + "false";
        // 实现每间隔一段时间（一分钟）才能进行下一次请求的（当剩余时间剩余2分钟以下，就可以重新发, 重复）
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if(expire > 120)  return  "请求过于频繁，请稍候再试";
        }
        if(mapper.findAccountByNameOrEmail(email) != null) {
            // 根据邮箱给找到了存在的用户
            return "此邮箱已被注册";
        }
        return sendEmailHelper(key,email);
    }

    @Override
    public String updatePassword(String username, String email, String code, String newPassword, String sessionId) {
        String key = "email:" + sessionId + ":" + email + "true";请求
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            // 在这里进行是否验证的
            String s = stringRedisTemplate.opsForValue().get(key);
            if(s == null) return "请先获取验证码";
            if (s.equals(code)) {
                // 通过验证 通过之后便清掉验证码
                Account account = mapper.findAccountByNameOrEmail(email);
                Account account1 = mapper.findAccountByNameOrEmail(username);
                if (!account1.equals(account)) {
                    return "用户名或邮箱输入错误！";
                }
                if (mapper.updatePassword(username, bCryptPasswordEncoder.encode(newPassword)) < 0) {
                    return "重置密码失败，请联系管理员";
                } else {
                    return "success";
                }
            } else {
                return "验证码错误";
            }
        } else {
            return "请先获取验证码";
        }
    }

    @Override
    public String validateEmailAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + "false";
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            String s = stringRedisTemplate.opsForValue().get(key);
            if(s == null) return "请先获取验证码";
            if(s.equals(code)) {
                int res = mapper.createAccount(username, bCryptPasswordEncoder.encode(password), email);
                if(res > 0) return null;
                return "内部服务器错误请联系管理员";
            } else {
                return "验证码错误，请检查后在提交";
            }
        } else {
            return "请先获取验证码";
        }
    }

    @Override
    public String sendValidateEmailForForget(String username, String email, String sessionId) {
        String key = "email:" + sessionId + ":" + email + "true";
        // 实现每间隔一段时间（一分钟）才能进行下一次请求的（当剩余时间剩余2分钟以下，就可以重新发, 重复）
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if(expire > 120)  return  "请求过于频繁，请稍候再试";
        }
        if(mapper.findEmailByEmail(email) == null || mapper.findAccountByNameOrEmail(email) == null) {
            // 根据邮箱给找到了存在的用户
            return "找不到该用户！";
        }
        return sendEmailHelper(key, email);
    }

    public String sendEmailHelper(String key,String email) {
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
            return null;
        } catch (MailSendException e) {
            e.printStackTrace();
            return "验证码发送失败，请联系管理员";
        }
    }
}
