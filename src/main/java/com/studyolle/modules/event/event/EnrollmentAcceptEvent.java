package com.studyolle.modules.event.event;

import com.studyolle.modules.event.Enrollment;
import lombok.Getter;

@Getter
public class EnrollmentAcceptEvent extends EnrollmentEvent
{
    public EnrollmentAcceptEvent(Enrollment enrollment)
    {
        super(enrollment, "모임 참가 신청을 확인했습니다. 모임에 참석하세요.");
    }
}
