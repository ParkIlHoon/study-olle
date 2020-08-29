package com.studyolle.study;

import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1>Study 리포지토리</h1>
 */
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>
{
    boolean existsByPath(String path);
}
