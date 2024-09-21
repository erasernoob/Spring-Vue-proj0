package org.backend.service.Impl;

import jakarta.annotation.Resource;
import org.backend.entity.Account;
import org.backend.mapper.UserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 作为自定义用户密码以及信息，校验用户登录的service类 实现自UserDetailService
public class AuthorizeServiceImpl implements AuthorizedService {

    @Resource
    UserMapper mapper;

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
}
