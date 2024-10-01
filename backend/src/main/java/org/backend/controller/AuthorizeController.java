package org.backend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.backend.Listener.MailQueueListener;
import org.backend.entity.RestBean;
import org.backend.entity.vo.request.ConfirmResetVO;
import org.backend.entity.vo.request.EmailRegisterVO;
import org.backend.entity.vo.request.EmailResetVO;
import org.backend.service.AccountService;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@RequestMapping("/api/auth")
@Validated // 加入验证机制
@RestController
public class AuthorizeController  {

    @Resource
    AccountService accountService;
    @Autowired
    private MailQueueListener mailQueueListener;
    @Autowired
    private Queue mail;

    /**
     * register Controller
     *  使用json进行传输数据？？
     *  get请求使用表单请求参数进行接收
     *  post请求使用请求实体类进行接收
     */
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo ) {
        return messageHandler(() -> accountService.registerEmailAccount(vo));
    }

    /**
     * 返回对象代码优化
     */
    private RestBean<Void> messageHandler(Supplier<String> action)  {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(message, 400, null);
    }

    /**
     * 请求验证码controller
     */
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@Email @RequestParam String email,
                                        @RequestParam @Pattern(regexp = "(register|reset)") String type,
                                        HttpServletRequest request) {
        return this.messageHandler(() -> accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    private <T> RestBean<Void> messageHandler(T vo, Function<T, String> function) {
        return messageHandler(() -> function.apply(vo));
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody@Valid ConfirmResetVO vo) {
        return messageHandler(vo, accountService::resetConfirm);
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody@Valid EmailResetVO vo) {
        return messageHandler(vo, accountService::resetEmailAccountPassword);
    }

}
