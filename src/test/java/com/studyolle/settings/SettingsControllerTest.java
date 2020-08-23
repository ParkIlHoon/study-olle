package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach()
    {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 수정 폼")
    @WithAccount(value = "1hoon")
    void profile() throws Exception
    {
        mockMvc.perform(
                        get("/settings/profile")
                                .with(csrf())
                        )
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
        ;
    }

    @Test
    @DisplayName("프로필 수정하기 - 입력값 정상")
//    @WithUserDetails(value = "1hoon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithAccount(value = "1hoon")
    void profile_update() throws Exception
    {
        String bio = "수정";
        mockMvc.perform(
                        post("/settings/profile")
                            .param("bio", bio)
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"))
        ;

        Account account = accountRepository.findByNickname("1hoon");
        assertEquals(bio, account.getBio());
    }

    @Test
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @WithAccount(value = "1hoon")
    void profile_update_wrong_input_value() throws Exception
    {
        String bio = "수정하려는텍스트의 길이가 너무 길면 오류가 발생하는데 몇자리 까지 가능하게 했더라? 어쨋든 길이가 지정한 값보다 길기 때문에 에러가 발생해야함";
        mockMvc.perform(
                        post("/settings/profile")
                                .param("bio", bio)
                                .with(csrf())
                        )
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account account = accountRepository.findByNickname("1hoon");
        assertNotEquals(bio, account.getBio());
    }

    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount(value = "1hoon")
    void password() throws Exception
    {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @Test
    @DisplayName("패스워드 수정하기")
    @WithAccount(value = "1hoon")
    void password_update() throws Exception
    {
        String newPassword = "12345678";
        mockMvc.perform(
                        post("/settings/password")
                            .param("newPassword", newPassword)
                            .param("newPasswordConfirm", newPassword)
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"))
        ;
    }

    @Test
    @DisplayName("패스워드 수정하기 - 입력값 오류")
    @WithAccount(value = "1hoon")
    void password_update_with_wrong_input() throws Exception
    {
        String newPassword = "12345678";
        mockMvc.perform(
                        post("/settings/password")
                            .param("newPassword", newPassword)
                            .param("newPasswordConfirm", "11111111")
                            .with(csrf())
                        )
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors())
        ;
    }
}