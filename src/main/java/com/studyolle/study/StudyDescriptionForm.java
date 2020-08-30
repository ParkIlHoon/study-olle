package com.studyolle.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class StudyDescriptionForm
{
    @Length(max = 100)
    private String shortDescription;

    private String fullDescription;
}
