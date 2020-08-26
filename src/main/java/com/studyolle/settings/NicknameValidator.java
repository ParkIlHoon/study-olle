package com.studyolle.settings;

import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * <h1>NicknameForm 값 검증 클래스</h1>
 *
 * NicknameForm 데이터에 대한 값 검증을 수행하는 클래스
 *
 * @see com.studyolle.settings.NicknameForm
 * @see com.studyolle.settings.SettingsController
 */
@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator
{
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        NicknameForm nicknameForm = (NicknameForm) o;

        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());

        if (byNickname != null)
        {
            errors.rejectValue("nickname", "wrong.nickname", "이미 존재하는 닉네임입니다.");
        }
    }
}