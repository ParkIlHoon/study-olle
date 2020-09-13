package com.studyolle.modules.main;

import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * <h1>메인 컨트롤러</h1>
 *
 * 메인 페이지를 띄우는 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class MainController
{
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model)
    {
        if (account != null)
        {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

    /**
     * 이메일 로그인 폼 요청 메서드
     * @return
     */
    @GetMapping("/email-login")
    public String emailLogin()
    {
        return "email-login";
    }

    /**
     * 이메일 로그인 인증메일 발송 요청 메서드
     * @param email
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/email-login")
    public String emailLoginAction(String email, Model model, RedirectAttributes attributes)
    {
        Account byEmail = accountRepository.findByEmail(email);

        if (byEmail == null)
        {
            model.addAttribute("error", "존재하지 않는 사용자입니다.");
            return "email-login";
        }

        if (!byEmail.canSendConfirmEmail())
        {
            model.addAttribute("error", "이메일을 보낼 수 없는 상태입니다. 한 시간 뒤에 다시 시도해보세요.");
            return "email-login";
        }

        accountService.sendLoginLink(byEmail);
        attributes.addFlashAttribute("message", "메일로 로그인 링크를 발송했습니다.");
        return "redirect:/email-login";
    }

    /**
     * 이메일 링크 로그인 요청 메서드
     * @param token
     * @param email
     * @param model
     * @return
     */
    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model)
    {
        Account byEmail = accountRepository.findByEmail(email);

        if (byEmail == null || !byEmail.isValidToken(token))
        {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return "logged-in-by-email";
        }

        accountService.login(byEmail);
        return "logged-in-by-email";
    }

    @GetMapping("/search/study")
    public String searchStudy (String keyword,
                               Model model,
                               @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime")? "publishedDateTime" : "memberCount");
        return "search";
    }
}
