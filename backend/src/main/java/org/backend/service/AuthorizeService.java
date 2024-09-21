package org.backend.service;


import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    public boolean sendValidateEmail(String email, String sessionId);

}
