package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

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

    /**
     * 모임 정보 수정 메서드
     * @param event
     * @param eventForm
     */
    public void updateEvent(Event event, EventForm eventForm)
    {
        modelMapper.map(eventForm, event);

        //TODO 모집 인원을 늘린 선착순 모임의 경우, 자동으로 추가 인원의 참가 신청을 확정 상태로 변경해야함.
    }
}
