package com.studyolle.modules.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.infra.MockMvcTest;
import com.studyolle.modules.account.WithAccount;
import com.studyolle.modules.account.AccountFactory;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyFactory;
import com.studyolle.modules.study.StudyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
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
    private StudyRepository studyRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StudyFactory studyFactory;

    @Autowired
    private EventFactory eventFactory;

    @AfterEach
    void resetAllDatas ()
    {
        eventRepository.deleteAll();
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

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

    @Test
    @DisplayName("모임 뷰")
    @WithAccount(value = "1hoon")
    void view_event() throws Exception
    {
        Account account = accountRepository.findByNickname("1hoon");
        // 스터디 생성
        Study study = studyFactory.createStudy("test-study", account);
        // 이벤트 생성
        Event event = eventFactory.createEvent(study, account);

        mockMvc.perform(
                        get("/study/" + study.getEncodePath() + "/events/" + event.getId())
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("event/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("event"))
        ;
    }

    @Test
    @DisplayName("모임 정보 수정")
    @WithAccount(value = "1hoon")
    void edit_event() throws Exception
    {
        Account account = accountRepository.findByNickname("1hoon");
        // 스터디 생성
        Study study = studyFactory.createStudy("test-study", account);
        // 이벤트 생성
        Event event = eventFactory.createEvent(study, account);

        mockMvc.perform(
                        post("/study/" + study.getEncodePath() + "/events/" + event.getId() + "/edit")
                                .param("title", "테스트 모임 수정")
                                .param("limitOfEnrollments", "20")
                                .param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).toString())
                                .param("startDateTime", LocalDateTime.now().plusDays(2).toString())
                                .param("endDateTime", LocalDateTime.now().plusDays(17).toString())
                            .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/study/" + study.getEncodePath() + "/events/" + event.getId()))
        ;

        assertThat(event.getTitle()).isEqualTo("테스트 모임 수정");
        assertThat(event.getLimitOfEnrollments()).isEqualTo(20);
    }

    @Test
    @DisplayName("모임 취소")
    @WithAccount(value = "1hoon")
    void remove_event() throws Exception
    {
        Account account = accountRepository.findByNickname("1hoon");
        // 스터디 생성
        Study study = studyFactory.createStudy("test-study", account);
        // 이벤트 생성
        Event event = eventFactory.createEvent(study, account);

        mockMvc.perform(
                        delete("/study/" + study.getEncodePath() + "/events/" + event.getId())
                            .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/study/" + study.getEncodePath() + "/events"))
        ;

        Optional<Event> byId = eventRepository.findById(event.getId());
        assertThat(byId.isEmpty()).isTrue();
    }
}