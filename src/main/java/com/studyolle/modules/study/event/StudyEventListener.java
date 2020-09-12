package com.studyolle.modules.study.event;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountPredicates;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

/**
 * <h1>스터디 이벤트 리스너 클래스</h1>
 *
 * @see com.studyolle.modules.study.event.StudyCreatedEvent
 */
@Component
@Slf4j
@Transactional
@Async
@RequiredArgsConstructor
public class StudyEventListener
{
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    /**
     * 스터디 개설 이벤트 핸들러 메서드
     * @param event
     */
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent event)
    {
        log.info(event.getStudy().getTitle() + " is Created.");
        Study study = studyRepository.findStudyWithTagsAndZonesById(event.getStudy().getId());
        Iterable<Account> all = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        all.forEach(account -> {
            if (account.isStudyCreatedByEmail())
            {
                sendStudyCreatedEmail(study, account);
            }
            if (account.isStudyCreatedByWeb())
            {
                saveStudyCreatedNotification(study, account);
            }
        });
    }

    /**
     * 스터디 개설 알림 정보 저장 메서드
     * @param study
     * @param account
     */
    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = Notification.builder()
                                                    .title(study.getTitle() + " 가 개설됨")
                                                    .link("/study/" + study.getEncodePath())
                                                    .checked(false)
                                                    .createdDateTime(LocalDateTime.now())
                                                    .message(study.getShortDescription())
                                                    .account(account)
                                                    .notificationType(NotificationType.STUDY_CREATED)
                                                .build();

        notificationRepository.save(notification);
    }

    /**
     * 스터디 개설 알림 메일 발송 메서드
     * @param study
     * @param account
     */
    private void sendStudyCreatedEmail(Study study, Account account)
    {
        Context context = new Context();
        context.setVariable("link", "/study/" + study.getEncodePath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 개설되었습니다.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                                                    .subject("[스터디올래] " + study.getTitle() + " 스터디가 개설되었습니다.")
                                                    .to(account.getEmail())
                                                    .message(message)
                                                .build();
        emailService.sendEmail(emailMessage);
    }
}
