package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

        // 회원 가입
        Account account = accountService.joinAccount(signUpForm);
        // 로그인 처리
        accountService.login(account);

        return "redirect:/";
    }

    /**
     * 인증 메일 확인 요청
     * @param token 이메일 인증 토큰
     * @param email 이메일 주소
     * @return
     */
    @GetMapping("/check-email-token")
    public String checkEmailToken (String token, String email, Model model)
    {
        String viewName = "account/checked-email";
        Account byEmail = accountRepository.findByEmail(email);

        // 이메일 검사
        if (byEmail == null)
        {
            model.addAttribute("error", "wrong.email");
            return viewName;
        }
        // 토큰 검사
        if (!byEmail.isValidToken(token))
        {
            model.addAttribute("error", "wrong.token");
            return viewName;
        }

        // 사용자 인증 및 로그인 처리
        accountService.completeSignUp(byEmail);

        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", byEmail.getNickname());

        return viewName;
    }

    /**
     * 이메일 인증 확인 페이지 요청
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/check-email")
    public String checkEmail (@CurrentUser Account account, Model model)
    {
        model.addAttribute("email", account.getEmail());

        return "account/check-email";
    }

    /**
     * 이메일 인증메일 재발송 요청
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail (@CurrentUser Account account, Model model)
    {
        if (!account.canSendConfirmEmail())
        {
            model.addAttribute("error", "인증 메일은 한 시간에 한 번만 보낼 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    /**
     * 프로필 페이지 요청
     * @param nickname 프로필을 조회할 사용자의 닉네임
     * @param model
     * @param account
     * @return
     */
    @GetMapping("/profile/{nickname}")
    public String viewProfile (@PathVariable(value = "nickname") String nickname, Model model, @CurrentUser Account account)
    {
        Account byNickname = accountRepository.findByNickname(nickname);

        if (byNickname == null)
        {
            throw new IllegalArgumentException(nickname + " 에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute("account", byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));

        return "account/profile";
    }
}
