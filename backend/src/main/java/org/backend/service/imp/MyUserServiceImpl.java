package org.backend.service.imp;

import jakarta.annotation.Resource;
import org.backend.service.MyUserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyUserServiceImpl implements MyUserService {

   @Resource


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null) {
            return null;
        }
        return User.builder().username(username).password("password").build();
    }
}
