package org.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.backend.entity.Account;
import org.backend.entity.AccountUser;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    @Select("select * from user where username = #{text} or email = #{text}")
    AccountUser findAccountUserByNameOrEmail(String text);

    @Insert("insert into user(email, username, password) values(#{email}, #{username}, #{password})")
    int createAccount(String username, String password, String email);

    @Select("select email from user where email=#{email}")
    String findEmailByEmail(String email);

    @Select("select * from user where username=#{username} and email=#{email}")
    Account findUserByUsername(String username, String email);

    @Update("update user set password=#{newPassword} where username=#{username}")
    int updatePassword(String username, String newPassword);

}
