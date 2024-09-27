package org.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.backend.utils.BaseData;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("AccountDto")
@AllArgsConstructor
public class AccountDto implements BaseData {

    @TableId(type = IdType.AUTO)
    Integer id; // 因为要使用element-plus, 需要使用包装类

    String username;
    String password;
    String email;
    String role;
    Date registerDate;
}
