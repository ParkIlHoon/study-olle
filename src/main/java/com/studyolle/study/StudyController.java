package com.studyolle.study;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * <h1>스터디 컨트롤러</h1>
 */
@Controller
@RequiredArgsConstructor
public class StudyController
{
    private final StudyFormValidator studyFormValidator;
    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    @InitBinder("studyForm")
    void initBind(WebDataBinder binder)
    {
        binder.addValidators(studyFormValidator);
    }

    /**
     * 스터디 개설 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/new-study")
    public String studyForm(@CurrentUser Account account, Model model)
    {
        model.addAttribute("account", account);
        model.addAttribute("studyForm", new StudyForm());

        return "study/form";
    }

    /**
     * 스터디 개설 요청 메서드
     * @param account
     * @param model
     * @param studyForm
     * @param errors
     * @return
     */
    @PostMapping("/new-study")
    public String newStudy(@CurrentUser Account account,
                           Model model,
                           @Valid @ModelAttribute StudyForm studyForm,
                           Errors errors)
    {
        if(errors.hasErrors())
        {
            return "study/form";
        }

        Study newStudy = studyService.createNewStudy(account, modelMapper.map(studyForm, Study.class));

        return "redirect:/study/" + newStudy.getEncodePath();
    }

    /**
     * 스터디 조회 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study byPath = studyService.getStudy(path);

        model.addAttribute("account", account);
        model.addAttribute("study", byPath);

        return "study/view";
    }

    /**
     * 스터디 멤버 조회 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/study/{path}/members")
    public String viewMembers(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study byPath = studyService.getStudy(path);

        model.addAttribute("account", account);
        model.addAttribute("study", byPath);

        return "study/members";
    }

    /**
     * 스터디 소개 수정 폼 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/study/{path}/settings/description")
    public String updateDescriptionForm(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study byPath = studyService.getStudy(path);
        StudyDescriptionForm form  = StudyDescriptionForm.builder()
                                                            .fullDescription(byPath.getFullDescription())
                                                            .shortDescription(byPath.getShortDescription())
                                                        .build();

        model.addAttribute("account", account);
        model.addAttribute("study", byPath);
        model.addAttribute("studyDescriptionForm", form);

        return "study/settings/description";
    }

    /**
     * 스터디 소개 수정 요청 메서드
     * @param account
     * @param path
     * @param studyDescriptionForm
     * @param errors
     * @return
     */
    @PostMapping("/study/{path}/settings/description")
    public String updateDescription(@CurrentUser Account account,
                                    @PathVariable String path,
                                    @Valid @ModelAttribute StudyDescriptionForm studyDescriptionForm,
                                    Errors errors,
                                    Model model,
                                    RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdate(account, path);

        if(errors.hasErrors())
        {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "study/settings/description";
        }

        studyService.updateStudyDescription(study, studyDescriptionForm);

        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/description";
    }

    /**
     * 배너 이미지 수정 폼 요청
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/study/{path}/settings/banner")
    public String updateBannerForm(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study byPath = studyService.getStudy(path);
        model.addAttribute("account", account);
        model.addAttribute("study", byPath);

        return "study/settings/banner";
    }

    /**
     * 스터디 배너 사용여부 변경 요청 메서드
     * @param account
     * @param path
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/banner/enable")
    public String updateBannerEnable(@CurrentUser Account account,
                                     @PathVariable String path,
                                     Model model,
                                     RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdate(account, path);

        studyService.updateStudyBannerEnable(study, true);

        attributes.addFlashAttribute("message", "이제 스터디 배너를 사용합니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    /**
     * 스터디 배너 사용여부 변경 요청 메서드
     * @param account
     * @param path
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/banner/disable")
    public String updateBannerDisable(@CurrentUser Account account,
                                     @PathVariable String path,
                                     Model model,
                                     RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdate(account, path);

        studyService.updateStudyBannerEnable(study, false);

        attributes.addFlashAttribute("message", "이제 스터디 배너를 사용하지 않습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    /**
     * 스터디 배너 변경 요청 메서드
     * @param account
     * @param path
     * @param image
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/banner")
    public String updateBanner(@CurrentUser Account account,
                               @PathVariable String path,
                               String image,
                               Model model,
                               RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdate(account, path);

        studyService.updateStudyBanner(study, image);

        attributes.addFlashAttribute("message", "스터디 배너를 수정했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }
}
