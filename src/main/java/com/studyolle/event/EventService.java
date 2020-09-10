package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.enrollment.EnrollmentRepository;
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
    private final EnrollmentRepository enrollmentRepository;

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

    /**
     * 모임 삭제 메서드
     * @param event
     */
    public void deleteEvent(Event event)
    {
        eventRepository.delete(event);
    }

    /**
     * 모임 참가 신청 메서드
     * @param event
     * @param account
     */
    public void enrollEvent(Event event, Account account)
    {
        // 해당 모임에 사용자가 이미 추가되었는지 확인
        boolean exists = enrollmentRepository.existsByEventAndAccount(event, account);

        if (!exists)
        {
            Enrollment enrollment = Enrollment.builder()
                                                    .account(account)
                                                    .event(event)
                                                    .accepted(event.isAbleToAcceptWaitingEnrollment())
                                                    .attended(false)
                                                    .enrolledAt(LocalDateTime.now())
                                                .build();

            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }

    }

    /**
     * 모임 참가 취소 메서드
     * @param event
     * @param account
     */
    public void disenrollEvent(Event event, Account account)
    {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account).orElseThrow();

        event.removeEnrollment(enrollment);
        enrollmentRepository.delete(enrollment);

        event.acceptTheFirstWaitingEnrollment();
    }

    /**
     * 신청 수락 메서드
     * @param event
     * @param enrollment
     */
    public void acceptEnrollment(Event event, Enrollment enrollment)
    {
        event.accept(enrollment);
    }
}
