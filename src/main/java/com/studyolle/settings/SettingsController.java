package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.tag.TagRepository;
import com.studyolle.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder binder)
    {
        binder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void initBinder_nickname(WebDataBinder binder)
    {
        binder.addValidators(nicknameValidator);
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

    /**
     * 닉네임 변경 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/account")
    public String nicknameForm (@CurrentUser Account account, Model model)
    {
        model.addAttribute("account", account);
        model.addAttribute("nicknameForm", modelMapper.map(account, NicknameForm.class));

        return "settings/account";
    }

    /**
     * 닉네임 변경 요청 메서드
     * @param account
     * @param nicknameForm
     * @param errors
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/settings/account")
    public String updateNickname (@CurrentUser Account account,
                                  @Valid @ModelAttribute NicknameForm nicknameForm,
                                  Errors errors,
                                  Model model,
                                  RedirectAttributes attributes)
    {
        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            return "settings/account";
        }

        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임이 정상적으로 변경되었습니다.");
        return "redirect:/settings/account";
    }

    /**
     * 태그 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/tags")
    public String updateTagForm (@CurrentUser Account account, Model model) throws Exception
    {
        Set<Tag> tags = accountService.getTags(account);

        List<String> collect = tagRepository.findAll().stream().map(tag -> tag.getTitle()).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));
        model.addAttribute("tags", tags.stream().map(tag -> tag.getTitle()).collect(Collectors.toList()));

        return "settings/tags";
    }

    /**
     * 태그 입력 요청 메서드
     * @param account
     * @param tagForm
     * @return
     */
    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTag (@CurrentUser Account account, @RequestBody TagForm tagForm)
    {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if(tag == null)
        {
            tag = tagRepository.save(Tag.builder().title(title).build());
        }

        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    /**
     * 태그 삭제 요청 메서드
     * @param account
     * @param tagForm
     * @return
     */
    @PostMapping("/settings/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag (@CurrentUser Account account, @RequestBody TagForm tagForm)
    {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if(tag == null)
        {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    /**
     * 지역 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/settings/zones")
    public String updateZoneForm (@CurrentUser Account account, Model model) throws Exception
    {
        Set<Zone> zones = accountService.getZones(account);
        List<String> collect = zoneRepository.findAll().stream().map(zone -> zone.toString()).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        return "settings/zones";
    }

    /**
     * 지역 입력 요청 메서드
     * @param account
     * @param zoneForm
     * @return
     */
    @PostMapping("/settings/zones/add")
    @ResponseBody
    public ResponseEntity addZone (@CurrentUser Account account, @RequestBody ZoneForm zoneForm)
    {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if (zone == null)
        {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    /**
     * 지역 삭제 요청 메서드
     * @param account
     * @param zoneForm
     * @return
     */
    @PostMapping("/settings/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone (@CurrentUser Account account, @RequestBody ZoneForm zoneForm)
    {

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if(zone == null)
        {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}
