package com.studyolle.infra.mail;

/**
 * <h1>이메일 발송 인터페이스</h1>
 *
 * @see com.studyolle.infra.mail.ConsoleMailService
 * @see com.studyolle.infra.mail.SmtpMailService
 */
public interface EmailService
{
    /**
     * 메일을 발송한다.
     * @param emailMessage 발송할 메일 메시지 객체
     */
    void sendEmail(EmailMessage emailMessage);
}
