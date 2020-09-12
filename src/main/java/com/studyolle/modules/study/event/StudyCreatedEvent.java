package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * <h1>스터디 생성 이벤트 클래스</h1>
 *
 * @see com.studyolle.modules.study.StudyService createNewStudy 메서드에서 이벤트 발생
 */
@Data
public class StudyCreatedEvent
{
    private Study study;

    public StudyCreatedEvent(Study newStudy)
    {
        this.study = newStudy;
    }
}
