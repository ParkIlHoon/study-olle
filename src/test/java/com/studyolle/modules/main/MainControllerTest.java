package com.studyolle.modules.main;

import com.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.infra.MockMvcTest;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class MainControllerTest extends AbstractContainerBaseTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void createAccount ()
    {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("1hoon");
        signUpForm.setEmail("chiwoo2074@gmail.com");
        signUpForm.setPassword("11111111");

        accountService.joinAccount(signUpForm);
    }

    @AfterEach
    void removeAccount ()
    {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 로그인")
    void login_with_email() throws Exception
    {
        mockMvc.perform(
                        post("/login")
                            .param("username", "chiwoo2074@gmail.com")
                            .param("password", "11111111")
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("1hoon"));
    }

    @Test
    @DisplayName("닉네임 로그인")
    void login_with_nickname() throws Exception
    {
        mockMvc.perform(
                        post("/login")
                            .param("username", "1hoon")
                            .param("password", "11111111")
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("1hoon"));
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception
    {
        mockMvc.perform(
                        post("/login")
                            .param("username", "2hoon")
                            .param("password", "11111111")
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser
    void logout() throws Exception
    {
        mockMvc.perform(
                        post("/logout")
                            .with(csrf())
                        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}