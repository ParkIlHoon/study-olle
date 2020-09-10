package com.studyolle.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.form.StudyForm;
import com.studyolle.modules.study.validator.StudyFormValidator;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.account.form.TagForm;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.ZoneRepository;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

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

    /**
     * 스터디 태그 폼 요청
     * @param account
     * @param path
     * @param model
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/study/{path}/settings/tags")
    public String updateTagForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException
    {
        Study study = studyService.getStudyForUpdate(account, path);
        List<String> collect = tagRepository.findAll().stream().map(tag -> tag.getTitle()).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));
        model.addAttribute("tags", study.getTags().stream().map(tag -> tag.getTitle()).collect(Collectors.toList()));

        return "study/settings/tags";
    }

    /**
     * 태그 입력 요청 메서드
     * @param account
     * @param tagForm
     * @return
     */
    @PostMapping("/study/{path}/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTag (@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm)
    {
        Study study = studyService.getStudyForUpdateTags(account, path);
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if(tag == null)
        {
            tag = tagRepository.save(Tag.builder().title(title).build());
        }

        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }

    /**
     * 태그 삭제 요청 메서드
     * @param account
     * @param tagForm
     * @return
     */
    @PostMapping("/study/{path}/settings/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag (@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm)
    {
        Study study = studyService.getStudyForUpdateTags(account, path);
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if(tag == null)
        {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    /**
     * 지역 폼 요청 메서드
     * @param account
     * @param model
     * @return
     */
    @GetMapping("/study/{path}/settings/zones")
    public String updateZoneForm (@CurrentUser Account account, @PathVariable String path, Model model) throws Exception
    {
        Study study = studyService.getStudyForUpdate(account, path);
        List<String> collect = zoneRepository.findAll().stream().map(zone -> zone.toString()).collect(Collectors.toList());

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(collect));
        model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));

        return "study/settings/zones";
    }

    /**
     * 지역 입력 요청 메서드
     * @param account
     * @param zoneForm
     * @return
     */
    @PostMapping("/study/{path}/settings/zones/add")
    @ResponseBody
    public ResponseEntity addZone (@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm)
    {
        Study study = studyService.getStudyForUpdateZones(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if (zone == null)
        {
            return ResponseEntity.badRequest().build();
        }

        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    /**
     * 지역 삭제 요청 메서드
     * @param account
     * @param zoneForm
     * @return
     */
    @PostMapping("/study/{path}/settings/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone (@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm)
    {
        Study study = studyService.getStudyForUpdateZones(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

        if(zone == null)
        {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }

    /**
     * 스터디 정보 수정 폼 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/study/{path}/settings/study")
    public String updateStudyForm(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);

        return "study/settings/study";
    }

    /**
     * 스터디 공개 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @PostMapping("/study/{path}/settings/study/publish")
    public String publishStudy(@CurrentUser Account account,
                               @PathVariable String path,
                               Model model,
                               RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        studyService.publishStudy(study);
        attributes.addFlashAttribute("message", "스터디를 공개했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    /**
     * 스터디 종료 요청 메서드
     * @param account
     * @param path
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/study/close")
    public String closeStudy(@CurrentUser Account account,
                             @PathVariable String path,
                             Model model,
                             RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        studyService.closeStudy(study);
        attributes.addFlashAttribute("message", "스터디를 종료했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    /**
     * 스터디 팀원 모집 시작 요청 메서드
     * @param account
     * @param path
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/recruit/start")
    public String startRecruiting(@CurrentUser Account account,
                                  @PathVariable String path,
                                  Model model,
                                  RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        if(!study.isRecruitUpdatable())
        {
            attributes.addFlashAttribute("message", "팀원 모집 정보는 1시간에 한 번만 변경할 수 있습니다.");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }

        studyService.startRecruiting(study);
        attributes.addFlashAttribute("message", "이제 스터디 팀원을 모집합니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    /**
     * 스터디 팀원 모집 종료 요청 메서드
     * @param account
     * @param path
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/study/{path}/settings/recruit/stop")
    public String stopRecruiting(@CurrentUser Account account,
                                  @PathVariable String path,
                                  Model model,
                                  RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        if(!study.isRecruitUpdatable())
        {
            attributes.addFlashAttribute("message", "팀원 모집 정보는 1시간에 한 번만 변경할 수 있습니다.");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }

        studyService.stopRecruiting(study);
        attributes.addFlashAttribute("message", "이제 스터디 팀원 모집을 종료합니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    /**
     * 스터디 경로 변경 요청 메서드
     * @param account
     * @param path
     * @param newPath
     * @param model
     * @param attributes
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/study/{path}/settings/study/path")
    public String updateStudyPath(@CurrentUser Account account,
                                  @PathVariable String path,
                                  String newPath,
                                  Model model,
                                  RedirectAttributes attributes) throws UnsupportedEncodingException
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        if (!studyService.isValidPath(newPath))
        {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("studyPathError", "이미 존재하거나 유효하지 않은 스터디 경로입니다.");
            return "study/settings/study";
        }

        studyService.updateStudyPath(study, newPath);
        attributes.addFlashAttribute("message", "스터디 경로를 변경했습니다.");
        return "redirect:/study/" + URLEncoder.encode(newPath, "UTF-8") + "/settings/study";
    }

    /**
     * 스터디 이름 변경 요청 메서드
     * @param account
     * @param path
     * @param newTitle
     * @param model
     * @param attributes
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/study/{path}/settings/study/title")
    public String updateStudyTitle(@CurrentUser Account account,
                                  @PathVariable String path,
                                  String newTitle,
                                  Model model,
                                  RedirectAttributes attributes) throws UnsupportedEncodingException
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        if (!studyService.isValidTitle(newTitle))
        {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("studyTitleError", "유효하지 않은 스터디 이름입니다.");
            return "study/settings/study";
        }

        studyService.updateStudyTitle(study, newTitle);
        attributes.addFlashAttribute("message", "스터디 이름을 변경했습니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    /**
     * 스터디 삭제 요청 메서드
     * @param account
     * @param path
     * @return
     */
    @PostMapping("/study/{path}/settings/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        studyService.removeStudy(study);
        return "redirect:/";
    }

    /**
     * 스터디 가입 요청 메서드
     * @param account
     * @param path
     * @return
     */
    @GetMapping("/study/{path}/join")
    public String joinStudy(@CurrentUser Account account, @PathVariable String path)
    {
        Study study = studyService.getStudyForJoin(account, path);
        studyService.addMember(account, study);

        return "redirect:/study/" + study.getEncodePath() + "/members";
    }

    /**
     * 스터디 탈퇴 요청 메서드
     * @param account
     * @param path
     * @return
     */
    @GetMapping("/study/{path}/leave")
    public String leaveStudy(@CurrentUser Account account, @PathVariable String path)
    {
        Study study = studyService.getStudyForJoin(account, path);
        studyService.removeMember(account, study);

        return "redirect:/study/" + study.getEncodePath() + "/members";
    }
}
