package org.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class EmailResetVO {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String email_code;
    String password;

}
