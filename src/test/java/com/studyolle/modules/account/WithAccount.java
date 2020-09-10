package com.studyolle.modules.account;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <h1>테스트를 위한 사용자 인증 어노테이션</h1>
 *
 * value 에 해당하는 nickname 을 가지는 사용자를 테스트 직전에 생성하고, Spring Security 로그인 처리도 해준다.<br>
 * 여러 테스트 수행 시, 앞서 수행된 테스트의 본 어노테이션으로 인해 사용자가 중복으로 생성되므로 @AfterEach 에서 데이터를 제거할 것.
 * @see WithAccountSecurityContext
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContext.class)
public @interface WithAccount
{
    String value();
}
