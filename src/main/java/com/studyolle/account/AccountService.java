package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.NicknameForm;
import com.studyolle.settings.Notifications;
import com.studyolle.settings.PasswordForm;
import com.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <h1>Account 서비스 클래스</h1>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService
{
    private final AccountRepository accountRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

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
    public void sendSignUpConfirmEmail(Account account)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("[StudyOlle] 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + account.getEmailCheckToken()
                                            + "&email=" + account.getEmail());

        mailSender.send(mailMessage);

        account.setConfirmMailSendDate(LocalDateTime.now());
    }

    /**
     * 로그인 처리 메서드
     * @param account
     */
    public void login(Account account)
    {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    /**
     * Spring Security 에서 로그인 처리를 위해 사용자를 조회하는 메서드
     * @param emailOrNick
     * @return
     * @throws UsernameNotFoundException
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNick) throws UsernameNotFoundException
    {
        Account account = accountRepository.findByEmail(emailOrNick);

        if (account == null)
        {
            account = accountRepository.findByNickname(emailOrNick);
        }

        if (account == null)
        {
            throw new UsernameNotFoundException(emailOrNick);
        }

        return new UserAccount(account);
    }

    /**
     * 사용자 이메일 인증 처리 메서드
     * @param byEmail
     */
    public void completeSignUp(Account byEmail)
    {
        byEmail.verifyEmail();
        login(byEmail);
    }

    /**
     * 사용자 프로필 정보 수정 메서드
     * @param account 수정할 사용자의 Account 객체
     * @param profile 수정할 내용
     */
    public void updateProfile(Account account, Profile profile)
    {
        modelMapper.map(profile, account);

        accountRepository.save(account);
    }

    /**
     * 사용자 패스워드 변경 메서드
     * @param account 수정할 사용자의 Account 객체
     * @param passwordForm 수정할 패스워드
     */
    public void updatePassword(Account account, PasswordForm passwordForm)
    {
        account.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));

        accountRepository.save(account);
    }

    /**
     * 사용자 알림 설정 변경 메서드
     * @param account 수정할 사용자의 Account 객체
     * @param notifications 수정할 알림 설정
     */
    public void updateNotifications(Account account, Notifications notifications)
    {
        modelMapper.map(notifications, account);

        accountRepository.save(account);
    }

    /**
     * 사용자 닉네임 변경 메서드
     * @param account
     * @param nicknameForm
     */
    public void updateNickname(Account account, NicknameForm nicknameForm)
    {
        account.setNickname(nicknameForm.getNickname());
        accountRepository.save(account);
        login(account);
    }

    /**
     * 사용자 이메일 로그인 링크 메일 발송 메서드
     * @param account
     */
    public void sendLoginLink(Account account)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("[StudyOlle] 이메일 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken()
                                         + "&email=" + account.getEmail());

        mailSender.send(mailMessage);

        account.setConfirmMailSendDate(LocalDateTime.now());

        accountRepository.save(account);
    }

    /**
     * 사용자 태그 추가 메서드
     * @param account
     * @param tag
     */
    public void addTag(Account account, Tag tag)
    {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }
}
