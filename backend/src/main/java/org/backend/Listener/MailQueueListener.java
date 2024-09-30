package org.backend.Listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
/**
 * 消息队列监听器？？？？
 */

@Component
@RabbitListener(queues = "email")
public class MailQueueListener {

    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String mailUsername;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String)data.get("email");
        String code = (String) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type) {
            case "register" -> createSimpleMailMessage("欢迎注册Mclaren官方网站",
                    "这是你的注册验证码" + code, email);
            case "resetPassword" -> createSimpleMailMessage("来自Mclaren重置密码操作，是你吗？", "您的验证码为" + code, email);
            default -> null;
        };
        if(message == null) return;
        mailSender.send(message);
    }

    /**
     * 发送邮件辅助函数
     */
    private SimpleMailMessage createSimpleMailMessage(String subject, String content, String to) {
         SimpleMailMessage message = new SimpleMailMessage();
         message.setSubject(subject);
         message.setText(content);
         message.setTo(to);
         message.setFrom(mailUsername);
         return message;
    }
}
