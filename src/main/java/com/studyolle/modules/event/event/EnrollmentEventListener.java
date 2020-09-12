package com.studyolle.modules.event.event;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Enrollment;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import com.studyolle.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

/**
 * <h1>모임 이벤트 리스너 클래스</h1>
 *
 * @see com.studyolle.modules.event.event.EnrollmentEvent
 * @see com.studyolle.modules.event.event.EnrollmentAcceptEvent
 * @see com.studyolle.modules.event.event.EnrollmentRejectEvent
 */
@Component
@Async
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener
{
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;


    @EventListener
    public void enrollmentAcceptEvent(EnrollmentAcceptEvent e)
    {
        Enrollment enrollment = e.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if(account.isStudyEnrollmentResultByWeb())
        {
            saveStudyEnrollmentResultNotification(e, account, event, study);
        }
        if(account.isStudyEnrollmentResultByEmail())
        {
            sendStudyEnrollmentResultMail(e, account, event, study);
        }
    }

    @EventListener
    public void enrollmentRejectEvent(EnrollmentRejectEvent e)
    {
        Enrollment enrollment = e.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if(account.isStudyEnrollmentResultByWeb())
        {
            saveStudyEnrollmentResultNotification(e, account, event, study);
        }
        if(account.isStudyEnrollmentResultByEmail())
        {
            sendStudyEnrollmentResultMail(e, account, event, study);
        }
    }

    private void sendStudyEnrollmentResultMail(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study)
    {

        Context context = new Context();
        context.setVariable("link", "/study/" + study.getEncodePath() + "/events/" + event.getId());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());

        String email = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("[스터디올래] " + study.getTitle() + " 스터디의 " + event.getTitle() + " 모임에 " + enrollmentEvent.getMessage())
                .to(account.getEmail())
                .message(email)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void saveStudyEnrollmentResultNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study)
    {
        Notification notification = Notification.builder()
                                                    .title(study.getTitle() + " > " + event.getTitle())
                                                    .link("/study/" + study.getEncodePath() + "/events/" + event.getId())
                                                    .checked(false)
                                                    .createdDateTime(LocalDateTime.now())
                                                    .message(enrollmentEvent.getMessage())
                                                    .account(account)
                                                    .notificationType(NotificationType.EVENT_ENROLLMENT)
                                                .build();

        notificationRepository.save(notification);
    }
}
