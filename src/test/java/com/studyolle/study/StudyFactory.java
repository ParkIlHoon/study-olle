package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <h1>테스트를 위한 Study 사전 데이터 생성 Factory 클래스</h1>
 */
@Component
@RequiredArgsConstructor
public class StudyFactory
{
    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyRepository studyRepository;

    /**
     * 스터디 생성 메서드
     * @param path
     * @param manager
     * @return
     */
    public Study createStudy(String path, Account manager)
    {
        Study study = new Study();
        study.setPath(path);

        Study newStudy = studyService.createNewStudy(manager, study);
        return newStudy;
    }
}
