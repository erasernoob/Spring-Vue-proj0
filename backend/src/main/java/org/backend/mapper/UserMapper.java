package org.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.backend.entity.Account;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

}
