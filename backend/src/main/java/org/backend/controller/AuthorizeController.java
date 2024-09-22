package org.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.backend.entity.RestBean;
import org.backend.service.AuthorizeService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
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

    private final String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";

    /**
     * 验证邮箱地址的接口
     */
    @PostMapping("/valid-email")
    public RestBean<Void> validateEmail(@Email @RequestParam("email") String email, HttpSession session) {
        if(authorizeService.sendValidateEmail(email, session.getId()) == null) {
            return RestBean.success("邮件发送成功", 200);
        }
        return RestBean.failure(authorizeService.sendValidateEmail(email, session.getId()), 400);
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX)@Length(min = 2, max = 8) @RequestParam("username") String username,
                                         @Length(min = 6, max = 16)@RequestParam("password")String password,@Email@RequestParam("email") String email,
                                        @RequestParam("email_code") String code, HttpSession session) {
        String res = authorizeService.validateEmailAndRegister(username, password, email, code, session.getId());
        if(res == null)
            return RestBean.success("注册成功！", 200);
        else
            return RestBean.failure(res, 400);

    }

}
