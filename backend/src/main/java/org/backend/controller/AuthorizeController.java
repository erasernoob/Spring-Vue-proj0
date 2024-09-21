package org.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import org.backend.entity.RestBean;
import org.backend.service.AuthorizeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated // 验证validation的依赖导入
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AuthorizeService authorizeService;

    /**
     * 验证邮箱地址的
     */
    @PostMapping("valid-email")
    public RestBean<Void> validateEmail(@Email @RequestParam("email") String email, HttpSession session) {
        if(authorizeService.sendValidateEmail(email, session.getId())) {
            return RestBean.success("邮件发送成功", 200);
        }
        return RestBean.failure("邮件发送失败,请联系管理员", 400);
    }

}
