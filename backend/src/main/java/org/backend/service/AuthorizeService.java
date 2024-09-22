package org.backend.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    public String sendValidateEmail(String email, String sessionId);
    String validateEmailAndRegister(String username, String password, String email, String code, String sessionId);

}
