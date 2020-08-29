package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>Study 서비스 클래스</h1>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StudyService
{
    private final StudyRepository studyRepository;

    public Study createNewStudy(Account account, Study study)
    {
        study.addManager(account);
        return studyRepository.save(study);
    }
}
