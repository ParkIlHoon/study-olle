package com.studyolle.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>테스트를 위한 사전 Account 데이터 생성을 위한 Factory 클래스</h1>
 */
@Component
@RequiredArgsConstructor
public class AccountFactory
{
    private final AccountRepository accountRepository;

    /**
     * Account 신규 생성 메서드
     * @param nickName 생성할 사용자의 nickname
     * @return
     */
    public Account createAccount(String nickName)
    {
        Account account = Account.builder()
                                    .nickname(nickName)
                                    .email(nickName + "@gmail.com")
                                .build();
        accountRepository.save(account);
        return account;
    }
}
