package com.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * <h1>SignUpForm 값 검증 클래스</h1>
 *
 * SignUpForm 데이터에 대한 값 검증을 수행하는 클래스
 *
 * @see com.studyolle.account.SignUpForm
 * @see com.studyolle.account.AccountController
 */
@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator
{
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass)
    {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        SignUpForm signUpForm = (SignUpForm) errors;

        // 이메일 값 중복 체크
        if (accountRepository.existsByEmail(signUpForm.getEmail()))
        {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 존재하는 이메일입니다.");
        }
        // 닉네임 값 중복 체크
        if (accountRepository.existsByNickname(signUpForm.getNickname()))
        {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
