package com.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <h1>Account 관련 요청 처리 클래스</h1>
 */
@Controller
public class AccountController
{
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
}
