package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StudyUpdateEvent
{
    private Study study;
    private String message;

    public StudyUpdateEvent(Study study, String message)
    {
        this.study = study;
        this.message = message;
    }
}
