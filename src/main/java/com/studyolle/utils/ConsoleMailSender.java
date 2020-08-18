package com.studyolle.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleMailSender implements MailSender
{
    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException
    {
        log.info(simpleMailMessage.getText());
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException
    {

    }
}
