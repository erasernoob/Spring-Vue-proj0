package org.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO {
    @Email
    String email;
    @Length(max = 6, min = 6)
    String email_code;
    String username;
    @Length(min = 6, max = 15)
    String password;
}
