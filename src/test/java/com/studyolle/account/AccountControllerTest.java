package com.studyolle.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private MailSender mailSender;

    @Test
    @DisplayName("회원 가입 화면이 보이는지 테스트")
    void signUpForm() throws Exception
    {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated())
        ;
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signUpSubmit_wrong_input_value() throws Exception
    {
        mockMvc.perform(
                        post("/sign-up")
                                .param("nickname", "1hoon")
                                .param("email", "email...")
                                .param("password", "1234")
                            .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated())
        ;
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signUpSubmit_right_input_value() throws Exception
    {
        mockMvc.perform(
                        post("/sign-up")
                                .param("nickname", "1hoon")
                                .param("email", "chiwoo2074@gmail.com")
                                .param("password", "12345678")
                                .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("1hoon"))
        ;

        // DB 에 Account 가 정상적으로 저장되었는지
        Account account = accountRepository.findByEmail("chiwoo2074@gmail.com");
        assertNotNull(account);
        assertNotNull(account.getEmailCheckToken());
        assertNotEquals(account.getPassword(), "12345678");
        // 메일은 발송 되었는지
        then(mailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증 메일 확인 - 입력값 오류")
    void checkEmailToken_with_wrong_input() throws Exception
    {
        mockMvc.perform(
                        get("/check-email-token")
                            .param("token", "")
                            .param("email", "")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;
    }

    @Test
    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Transactional
    void checkEmailToken_with_right_input() throws Exception
    {
        Account account = Account.builder()
                                    .email("chiwoo2074@gmail.com")
                                    .nickname("1hoon")
                                    .password("12345678")
                                .build();

        Account save = accountRepository.save(account);

        save.generateEmailCheckToken();

        mockMvc.perform(
                        get("/check-email-token")
                            .param("token", save.getEmailCheckToken())
                            .param("email", save.getEmail())
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(authenticated().withUsername("1hoon"))
        ;
    }
}