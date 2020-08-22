package com.studyolle.main;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <h1>메인 컨트롤러</h1>
 *
 * 메인 페이지를 띄우는 컨트롤러
 */
@Controller
public class MainController
{
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model)
    {
        if (account != null)
        {
            model.addAttribute(account);
        }

        return "index";
    }
}
