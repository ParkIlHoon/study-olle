package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * <h1>Account - User 간 Adapter 클래스</h1>
 *
 * 도메인 Account 와 Spring Security의 User 를 연결해주는 Adapter 클래스.<br>
 * 로그인 처리시 principal 에 본 클래스를 지정해 매핑시킨다.<br>
 * @see com.studyolle.account.AccountService
 */
@Getter
public class UserAccount extends User
{
    private Account account;

    public UserAccount(Account account)
    {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
