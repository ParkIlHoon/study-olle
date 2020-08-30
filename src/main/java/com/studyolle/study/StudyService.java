package com.studyolle.study;

import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

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

        checkExists(path, byPath);

        return byPath;
    }

    private void checkExists(String path, Study byPath) {
        if (byPath == null)
        {
            throw new IllegalArgumentException(path + " 에 해당하는 스터디가 없습니다.");
        }
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

        checkIsManager(account, study);
        return study;
    }

    private void checkIsManager(Account account, Study study) {
        if (!account.isManagerOf(study))
        {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
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

    /**
     * 스터디 배너 사용여부 수정 메서드
     * @param study
     * @param enable
     */
    public void updateStudyBannerEnable(Study study, boolean enable)
    {
        study.setUseBanner(enable);
    }

    /**
     * 스터디 배너 변경 메서드
     * @param study
     * @param image
     */
    public void updateStudyBanner(Study study, String image)
    {
        study.setImage(image);
    }

    /**
     * 스터디 태그 추가 메서드
     * @param study
     * @param tag
     */
    public void addTag(Study study, Tag tag)
    {
        study.getTags().add(tag);
    }

    /**
     * 스터디 태그 제거 메서드
     * @param study
     * @param tag
     */
    public void removeTag(Study study, Tag tag)
    {
        study.getTags().remove(tag);
    }

    /**
     * 스터디 지역 추가 메서드
     * @param study
     * @param zone
     */
    public void addZone(Study study, Zone zone)
    {
        study.getZones().add(zone);
    }

    /**
     * 스터디 지역 제거 메서드
     * @param study
     * @param zone
     */
    public void removeZone(Study study, Zone zone)
    {
        study.getZones().remove(zone);
    }

    /**
     * 태그 수정을 위한 스터디 조회 메서드
     * @param account
     * @param path
     * @return
     */
    public Study getStudyForUpdateTags(Account account, String path)
    {
        Study byPath = studyRepository.findAccountWithTagsByPath(path);

        checkExists(path, byPath);
        checkIsManager(account, byPath);

        return byPath;
    }

    /**
     * 지역 수정을 위한 스터디 조회 메서드
     * @param account
     * @param path
     * @return
     */
    public Study getStudyForUpdateZones(Account account, String path)
    {
        Study byPath = studyRepository.findAccountWithZonesByPath(path);

        checkExists(path, byPath);
        checkIsManager(account, byPath);

        return byPath;
    }
}
