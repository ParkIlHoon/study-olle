package com.studyolle.study;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController
{
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
}
