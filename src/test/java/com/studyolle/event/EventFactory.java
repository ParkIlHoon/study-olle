package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.EventType;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * <h1>테스트를 위한 Event 사전 데이터 생성 Factory 클래스</h1>
 */
@Component
@RequiredArgsConstructor
public class EventFactory
{
    @Autowired
    private EventService eventService;

    public Event createEvent(Study study, Account account)
    {
        Event event = new Event();
        event.setTitle("테스트 이벤트");
        event.setEventType(EventType.FCFS);
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(event.getEndEnrollmentDateTime().plusDays(1));
        event.setEndDateTime(event.getStartDateTime().plusDays(15));
        event.setLimitOfEnrollments(10);

        Event newEvent = eventService.createEvent(event, study, account);
        return newEvent;
    }
}
