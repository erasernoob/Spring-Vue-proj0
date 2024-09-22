package org.backend.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@ToString
public class AccountUser {
    int id;
    String email;
    String username;
}
