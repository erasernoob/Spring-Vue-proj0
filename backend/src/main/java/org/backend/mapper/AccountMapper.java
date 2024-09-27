package org.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.backend.entity.dto.AccountDto;

@Mapper
public interface AccountMapper extends BaseMapper<AccountDto> {


}
