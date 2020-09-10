package com.studyolle.modules.study;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * <h1>StudyForm 값 검증 클래스</h1>
 *
 * StudyForm 데이터에 대한 값 검증을 수행하는 클래스
 *
 * @see com.studyolle.modules.study.StudyForm
 * @see com.studyolle.modules.study.StudyController
 */
@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator
{
    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass)
    {
        return aClass.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        StudyForm studyForm = (StudyForm) o;
        boolean isCollapsed = studyRepository.existsByPath(studyForm.getPath());

        if (isCollapsed)
        {
            errors.rejectValue("path", "wrong.path", "스터디 경로를 사용할 수 없습니다.");
        }
    }
}
