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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
}
