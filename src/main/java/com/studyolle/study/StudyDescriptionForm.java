package com.studyolle.study;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class StudyDescriptionForm
{
    private String shortDescription;

    private String fullDescription;
}
