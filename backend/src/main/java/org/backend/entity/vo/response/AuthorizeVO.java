package org.backend.entity.vo.response;

import lombok.Data;

/**
 * 需要返回给前端的用户的详细信息实体类
 * 规范返回的信息格式
 */
@Data
public class AuthorizeVO {
   String username;
   String token;
   String expire;
   String role;
}
