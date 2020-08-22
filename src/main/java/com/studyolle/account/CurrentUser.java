package com.studyolle.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>Spring Security 현재 로그인 사용자 주입 어노테이션</h1>
 *
 * RUNTIME 시점에서 메서드 PARAMETER 형태로 현재 로그인한 사용자의 Account 객체를 주입해준다.<br>
 * 현재 사용자가 로그인 상태인 경우 {@link com.studyolle.domain.Account} 객체를 주입하고, 로그인 상태가 아닌 경우 Null 을 주입한다.<br>
 * <br>
 * expression에서 'anonymousUser' 는 로그인하지 않은 사용자를 뜻하는 고정 문자열이고,<br>
 * account는 Account 객체를 의미하며, {@link com.studyolle.account.UserAccount} 의 account 변수명과 동일해야한다.
 * @see com.studyolle.account.UserAccount
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser'? null : account")
public @interface CurrentUser
{

}
