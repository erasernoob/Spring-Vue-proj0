package org.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.backend.entity.dto.AccountDto;
import org.backend.entity.vo.request.ConfirmResetVO;
import org.backend.entity.vo.request.EmailRegisterVO;
import org.backend.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;
public interface AccountService extends IService<AccountDto>, UserDetailsService {
     AccountDto findAccountByNameOrEmail(String text);
     String registerEmailVerifyCode(String type, String email, String ip);
     String registerEmailAccount(EmailRegisterVO emailRegisterVO);
     String resetConfirm(ConfirmResetVO vo);
     String resetEmailAccountPassword(EmailResetVO vo);

}
