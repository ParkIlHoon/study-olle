package com.studyolle.modules.study.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>스터디 생성 이벤트 리스너 클래스</h1>
 *
 * @see com.studyolle.modules.study.event.StudyCreatedEvent
 */
@Component
@Slf4j
@Transactional(readOnly = true)
@Async
public class StudyEventListener
{
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent event)
    {
        log.info(event.getStudy().getTitle() + " is Created.");
        //TODO 이메일 보내거나 DB에 Notification 정보 저장
    }
}
