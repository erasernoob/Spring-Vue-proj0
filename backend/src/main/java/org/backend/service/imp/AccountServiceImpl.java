package org.backend.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.backend.entity.dto.AccountDto;
import org.backend.mapper.AccountMapper;
import org.backend.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDto> implements AccountService {

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
}
