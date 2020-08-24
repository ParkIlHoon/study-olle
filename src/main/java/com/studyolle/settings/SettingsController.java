package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * <h1>사용자 설정 컨트롤러</h1>
 *
 */
@Controller
@RequiredArgsConstructor
public class SettingsController
{
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder binder)
    {
        binder.addValidators(new PasswordFormValidator());
    }

    /**
     * 프로필 수정 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model)
    {
         model.addAttribute("account", account);
         model.addAttribute("profile", modelMapper.map(account, Profile.class));

         return "settings/profile";
    }

    /**
     * 프로필 수정 요청 메서드
     * @param account 현재 접속중인 사용자 Account 객체
     * @param profile 입력받은 프로필 정보 객체
     * @param errors Validation 결과
     * @param model
     * @param attributes redirection 시 일회성으로 전달할 파라미터
     * @return
     */
    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account,
                                @Valid @ModelAttribute Profile profile,
                                Errors errors,
                                Model model,
                                RedirectAttributes attributes)
    {
        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            return "settings/profile";
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "정상적으로 수정되었습니다.");

        return "redirect:/settings/profile";
    }

    /**
     * 패스워드 수정 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/password")
    public String passwordUpdateForm(@CurrentUser Account account, Model model)
    {
        model.addAttribute("account", account);
        model.addAttribute("passwordForm", new PasswordForm());

        return "settings/password";
    }

    /**
     * 패스워드 수정 요청 메서드
     * @param account
     * @param passwordForm
     * @param errors
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/settings/password")
    public String updatePassword(@CurrentUser Account account,
                                 @Valid @ModelAttribute PasswordForm passwordForm,
                                 Errors errors,
                                 Model model,
                                 RedirectAttributes attributes)
    {
        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            return "settings/password";
        }

        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message", "패스워드가 정상적으로 수정되었습니다.");
        return "redirect:/settings/password";
    }

    /**
     * 알림 설정 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/notifications")
    public String notificationForm (@CurrentUser Account account, Model model)
    {
        model.addAttribute("account", account);
        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));

        return "settings/notifications";
    }

    /**
     * 알림 설정 변경 요청 메서드
     * @param account
     * @param notifications
     * @param errors
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/settings/notifications")
    public String updateNotification(@CurrentUser Account account,
                                     @Valid @ModelAttribute Notifications notifications,
                                     Errors errors,
                                     Model model,
                                     RedirectAttributes attributes)
    {
        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            return "settings/notifications";
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정이 정상적으로 변경되었습니다.");
        return "redirect:/settings/notifications";
    }
}
