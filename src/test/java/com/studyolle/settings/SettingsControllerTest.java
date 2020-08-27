package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SettingsControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

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

    @Test
    @DisplayName("태그 수정 폼")
    @WithAccount(value = "1hoon")
    void update_tags_form() throws Exception
    {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @Test
    @DisplayName("태그 추가")
    @WithAccount(value = "1hoon")
    void add_tags() throws Exception
    {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("태그");

        mockMvc.perform(
                        post("/settings/tags/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagForm))
                            .with(csrf())
                        )
                .andExpect(status().isOk());

        Tag byTitle = tagRepository.findByTitle(tagForm.getTagTitle());
        assertNotNull(byTitle);
        assertTrue(accountRepository.findByNickname("1hoon").getTags().contains(byTitle));
    }

    @Test
    @DisplayName("태그 제거")
    @WithAccount(value = "1hoon")
    void remove_tags() throws Exception
    {
        Tag tag = Tag.builder().title("태그").build();
        tagRepository.save(tag);

        Account account = accountRepository.findByNickname("1hoon");
        accountService.addTag(account, tag);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle(tag.getTitle());

        mockMvc.perform(
                        post("/settings/tags/remove")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(tagForm))
                            .with(csrf())
                        )
                .andExpect(status().isOk());

        assertTrue(!account.getTags().contains(tag));
    }
}