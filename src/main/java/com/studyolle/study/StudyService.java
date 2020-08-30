package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
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
    private final ModelMapper modelMapper;
    private final StudyRepository studyRepository;

    /**
     * 스터디 개설 메서드
     * @param account
     * @param study
     * @return
     */
    public Study createNewStudy(Account account, Study study)
    {
        study.addManager(account);
        return studyRepository.save(study);
    }

    /**
     * 스터디 조회 메서드
     * @param path
     * @return
     * @throws IllegalArgumentException path에 해당하는 스터디가 없을 경우
     */
    public Study getStudy(String path)
    {
        Study byPath = studyRepository.findByPath(path);

        if (byPath == null)
        {
            throw new IllegalArgumentException(path + " 에 해당하는 스터디가 없습니다.");
        }

        return byPath;
    }

    /**
     * 수정을 위한 스터디 조회 메서드
     * @param account
     * @param path
     * @return
     * @throws AccessDeniedException 해당 스터디의 관리자가 아닌 경우
     */
    public Study getStudyForUpdate(Account account, String path)
    {
        Study study = this.getStudy(path);

        if (!account.isManagerOf(study))
        {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    /**
     * 스터디 소개 수정 메서드
     * @param study
     * @param studyDescriptionForm
     */
    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm)
    {
        modelMapper.map(studyDescriptionForm, study);
    }
}
