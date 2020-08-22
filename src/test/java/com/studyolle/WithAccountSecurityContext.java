package com.studyolle;

import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * <h1>@WithAccount 어노테이션 Context Factory 클래스</h1>
 *
 * WithAccount 의 value 로 지정한 값을 nickname 으로 가지는 사용자 Account 를 생성하고, Spring Security 로그인 처리 한다.
 * @see com.studyolle.WithAccount
 */
@RequiredArgsConstructor
public class WithAccountSecurityContext implements WithSecurityContextFactory<WithAccount>
{
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation)
    {
        String nickname = annotation.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname + "@gmail.com");
        signUpForm.setPassword("11111111");
        accountService.joinAccount(signUpForm);

        UserDetails userDetails = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}
