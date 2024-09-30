package org.backend.service.imp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.backend.entity.dto.AccountDto;
import org.backend.entity.vo.request.EmailRegisterVO;
import org.backend.mapper.AccountMapper;
import org.backend.service.AccountService;
import org.backend.utils.Const;
import org.backend.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDto> implements AccountService {

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate; // 消息队列引擎

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;
    @Autowired
    private SortHandlerMethodArgumentResolverCustomizer sortCustomizer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDto accountDto = findAccountByNameOrEmail(username);
        if(accountDto == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(accountDto.getUsername())
                .password(accountDto.getPassword())
                .roles("USER")
                .build();
    }

    @Override
    public AccountDto findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    public String generateVerifyCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 根据ip来对请求对象进行限制
     */
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, 60);
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) { // 为了线程安全
            // 判断是否存在ip限制
            if (!verifyLimit(ip)) {
                return "请求频繁，请稍后在尝试";
            }
            String code = generateVerifyCode();
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("email", data); // 加入消息队列
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, code, 3, TimeUnit.MINUTES); // 三分钟有效
            code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
            return null;
        }
    }

    /**
     *  在mybatis plus中判断是否存在该email
     */
    private boolean existAccountEmail(String email) {
        return this.baseMapper.exists(Wrappers.<AccountDto>query().eq("email", email));
    }

    private boolean existAccountUsername(String username) {
        return this.baseMapper.exists(Wrappers.<AccountDto>query().eq("username", username));
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        // 校验是否code是否正确
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getEmail_code())) {return "验证码错误，请重新输入";}
        // 判断邮件是否被注册过
        if(existAccountEmail(email)) {return "该邮件已被注册";}
        if(existAccountUsername(vo.getUsername())) {return "用户名已经被他人注册";}
        String password = passwordEncoder.encode(vo.getPassword()); // 加密
        AccountDto accountDto = new AccountDto(null, vo.getUsername(), password, email, "user", new Date());
        if(!this.save(accountDto)) {
            return "服务器内部错误，请联系管理员.";
        } else {
            // 删除掉已经注册成功的code
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        }
        return null;
    }
}
