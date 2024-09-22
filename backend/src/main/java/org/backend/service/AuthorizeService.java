package org.backend.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
     String sendValidateEmailForRegister(String email, String sessionId);
    String validateEmailAndRegister(String username, String password, String email, String code, String sessionId);
    String sendValidateEmailForForget(String username, String email, String sessionId);
    String updatePassword(String username, String email, String code, String newPassword,  String sessionId);
}
