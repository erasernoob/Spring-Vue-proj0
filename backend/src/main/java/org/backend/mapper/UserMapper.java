package org.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.backend.entity.Account;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    @Insert("insert into user(email, username, password) values(#{email}, #{username}, #{password})")
    int createAccount(String username, String password, String email);

}
