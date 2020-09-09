package com.studyolle.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountFactory;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.study.StudyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StudyFactory studyFactory;

    @Test
    @DisplayName("모임 생성")
    @WithAccount(value = "1hoon")
    void create_event() throws Exception
    {
        Account account = accountRepository.findByNickname("1hoon");
        // 스터디 생성
        Study study = studyFactory.createStudy("test-study", account);

        // 모임 생성 테스트
        mockMvc.perform(
                        post("/study/" + study.getEncodePath() + "/new-event")
                                .param("title", "테스트 모임")
                                .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).toString())
                                .param("startDateTime", LocalDateTime.now().plusDays(2).toString())
                                .param("endDateTime", LocalDateTime.now().plusDays(17).toString())
                            .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}