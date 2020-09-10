package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.form.StudyForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
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
        if (!study.isManagedBy(account))
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
        Study byPath = studyRepository.findStudyWithTagsByPath(path);

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
        Study byPath = studyRepository.findStudyWithZonesByPath(path);

        checkExists(path, byPath);
        checkIsManager(account, byPath);

        return byPath;
    }

    /**
     * 스터디 상태 수정을 위한 스터디 조회 메서드
     * @param account
     * @param path
     * @return
     */
    public Study getStudyForUpdateSelf(Account account, String path)
    {
        Study byPath = studyRepository.findStudyWithManagersByPath(path);

        checkExists(path, byPath);
        checkIsManager(account, byPath);

        return byPath;
    }

    /**
     * 스터디 공개 메서드
     * @param study
     */
    public void publishStudy(Study study)
    {
        study.publish();
    }

    /**
     * 스터디 종료 메서드
     * @param study
     */
    public void closeStudy(Study study)
    {
        study.close();
    }

    /**
     * 스터디 팀원 모집 시작 메서드
     * @param study
     */
    public void startRecruiting(Study study)
    {
        study.startRecruiting();
    }

    /**
     * 스터디 팀원 모집 종료 메서드
     * @param study
     */
    public void stopRecruiting(Study study)
    {
        study.stopRecruiting();
    }

    /**
     * 스터디 새 경로 유효성 검증 메서드
     * @param path
     * @return
     */
    public boolean isValidPath(String path)
    {
        if(path.matches(StudyForm.VALID_PATH_PATTERN))
        {
            return !studyRepository.existsByPath(path);
        }
        return false;
    }

    /**
     * 스터디 경로 변경 메서드
     * @param study
     * @param newPath
     */
    public void updateStudyPath(Study study, String newPath)
    {
        study.setPath(newPath);
    }

    /**
     * 스터디 새 이름 유효성 검증 메서드
     * @param title
     * @return
     */
    public boolean isValidTitle(String title)
    {
        return title.length() <= 50;
    }

    /**
     * 스터디 이름 변경 메서드
     * @param study
     * @param newTitle
     */
    public void updateStudyTitle(Study study, String newTitle)
    {
        study.setTitle(newTitle);
    }

    /**
     * 스터디 삭제 메서드
     * @param study
     */
    public void removeStudy(Study study)
    {
        if (study.isRemovable())
        {
            studyRepository.delete(study);
        }
        else
        {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    /**
     * 스터디 가입/탍퇴를 위한 스터디 조회 메서드
     * @param account
     * @param path
     * @return
     */
    public Study getStudyForJoin(Account account, String path)
    {
        Study byPath = studyRepository.findStudyWithMembersAndManagersByPath(path);
        checkExists(path, byPath);
        return byPath;
    }

    /**
     * 스터디 멤버 추가 메서드
     * @param account
     * @param study
     */
    public void addMember(Account account, Study study)
    {
        study.getMembers().add(account);
    }

    /**
     * 스터디 멤버 제거 메서드
     * @param account
     * @param study
     */
    public void removeMember(Account account, Study study)
    {
        study.getMembers().remove(account);
    }

    /**
     * 모임 참가신청을 위한 스터디 조회 메서드
     * @param path
     * @return
     */
    public Study getStudyForEnroll(String path)
    {
        Study study = studyRepository.findStudyOnlyByPath(path);
        checkExists(path, study);
        return study;
    }
}
