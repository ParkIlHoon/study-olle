package com.studyolle.settings;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController
{
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model)
    {
         model.addAttribute("account", account);
         model.addAttribute("profile", new Profile(account));

         return "settings/profile";
    }
}
