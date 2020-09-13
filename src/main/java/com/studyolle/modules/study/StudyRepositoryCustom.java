package com.studyolle.modules.study;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryCustom
{
    List<Study> findByKeyword(String keyword);
}
