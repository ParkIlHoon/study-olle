package com.studyolle.modules.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

/**
 * <h1>EventForm 값 검증 클래스</h1>
 *
 * EventForm 데이터에 대한 값 검증을 수행하는 클래스
 *
 * @see com.studyolle.modules.event.EventForm
 * @see com.studyolle.modules.event.EventController
 */
@Component
public class EventFormValidator implements Validator
{
    @Override
    public boolean supports(Class<?> aClass)
    {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        EventForm eventForm = (EventForm) o;

        if (isNotValidEnrollmentEndDateTime(eventForm))
        {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임의 접수 종료 일시를 정확히 입력해주세요.");
        }

        if (isNotValidEndDateTime(eventForm))
        {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임의 종료 일시를 정확히 입력해주세요.");
        }

        if (isNotValidStartDateTime(eventForm))
        {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임의 시작 일시를 정확히 입력해주세요.");
        }
    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors)
    {
        if (eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments())
        {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청보다 모집 인원수가 많아야합니다.");
        }
    }

    private boolean isNotValidStartDateTime(EventForm eventForm)
    {
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEnrollmentEndDateTime(EventForm eventForm)
    {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(EventForm eventForm)
    {
        return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime())
                || eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }
}
