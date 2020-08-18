package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * <h1>Account 관련 요청 처리 클래스</h1>
 */
@Controller
@RequiredArgsConstructor
public class AccountController
{
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final MailSender mailSender;

    /**
     * SignUpForm 데이터를 받았을 때, 바인딩 처리.
     * Validator 를 추가해 데이터가 SignUpForm 으로 바인딩될 때 값 검증을 수행한다.
     * @param webDataBinder
     */
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder)
    {
        webDataBinder.addValidators(signUpFormValidator);
    }

    /**
     * 계정 생성 폼 요청 처리 메서드
     * @param model
     * @return
     */
    @GetMapping("/sign-up")
    public String signUpForm (Model model)
    {
        // Form 구성
        model.addAttribute(new SignUpForm());

        return "account/sign-up";
    }

    /**
     * 회원 가입 요청
     * @param signUpForm 가입할 사용자 정보
     * @param errors validation 결과
     * @return
     */
    @PostMapping("/sign-up")
    public String signUpSubmit (@Valid @ModelAttribute SignUpForm signUpForm, Errors errors)
    {
        if (errors.hasErrors())
        {
            return "account/sign-up";
        }

        // 객체 생성
        Account account = Account.builder()
                                    .nickname(signUpForm.getNickname())
                                    .email(signUpForm.getEmail())
                                    //TODO encoding 해야함
                                    .password(signUpForm.getPassword())
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
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("[SturyOlle] 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                                            + "&email=" + newAccount.getEmail());

        mailSender.send(mailMessage);

        return "redirect:/";
    }
}
