package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *  <h1>Event 서비스 클래스</h1>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventService
{
    private final EventRepository eventRepository;

    /**
     * 모임 신규 생성 메서드
     * @param event
     * @param study
     * @param account
     * @return
     */
    public Event createEvent(Event event, Study study, Account account)
    {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);

        return eventRepository.save(event);
    }

}
