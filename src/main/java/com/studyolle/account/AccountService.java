package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <h1>Account 서비스 클래스</h1>
 */
@Service
@RequiredArgsConstructor
public class AccountService
{
    private final AccountRepository accountRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     * @param signUpForm 가입할 사용자 입력값 객체
     * @return 가입된 사용자 Account 객체
     */
    public Account joinAccount (SignUpForm signUpForm)
    {
        // 객체 생성
        Account account = Account.builder()
                                    .nickname(signUpForm.getNickname())
                                    .email(signUpForm.getEmail())
                                    //패스워드 encoding
                                    .password(passwordEncoder.encode(signUpForm.getPassword()))
                                    .joinedAt(LocalDateTime.now())
                                    .emailVerified(false)
                                    .studyCreatedByWeb(true)
                                    .studyEnrollmentResultByWeb(true)
                                    .studyUpdatedByWeb(true)
                                .build();

        // 저장
        Account newAccount = accountRepository.save(account);

        // 토큰 생성
        newAccount.generateEmailCheckToken();

        // 메일 전송
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    /**
     * 가입 인증 메일 발송 메서드
     * @param account 가입한 사용자
     */
    private void sendSignUpConfirmEmail(Account account)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("[StudyOlle] 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken()
                                            + "&email=" + account.getEmail());

        mailSender.send(mailMessage);
    }
}
