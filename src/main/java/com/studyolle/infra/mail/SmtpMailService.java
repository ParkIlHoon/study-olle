package com.studyolle.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * <h1>SMTP 메일 발송 서비스 클래스</h1>
 *
 * 실행환경(Profile)이 dev 일 때 Bean 으로 생성된다.<br>
 * 구글 SMTP 를 이용해 실제로 메일을 발송한다.
 */
@Component
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
public class SmtpMailService implements EmailService
{
    private final JavaMailSender mailSender;

    private final String sender = "studyOlle@noreply.com";

    @Override
    public void sendEmail(EmailMessage emailMessage)
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try
        {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(emailMessage.getTo());
            helper.setFrom(sender);
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getMessage(), true);

            mailSender.send(mimeMessage);
        }
        catch (MessagingException e)
        {
            log.error("email send has failed", e);
            throw new RuntimeException(e);
        }
    }
}
