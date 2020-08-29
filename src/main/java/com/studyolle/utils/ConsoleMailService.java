package com.studyolle.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * <h1>콘솔 메일 발송 서비스 클래스</h1>
 *
 * 실행환경(Profile)이 local 일 때 Bean 으로 생성된다.<br>
 * 메일을 실제 발송하지는 않고 콘솔에 출력한다.
 */
@Component
@Profile("local")
@Slf4j
public class ConsoleMailService implements EmailService
{
    @Override
    public void sendEmail(EmailMessage emailMessage)
    {
        log.info("sent email as console\n{}", emailMessage.getMessage());
    }
}
