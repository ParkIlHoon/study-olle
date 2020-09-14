package com.studyolle.modules.study;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * <h1>Study 리포지토리</h1>
 */
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom
{
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(value = "Study.withMembersAndManagers", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithMembersAndManagersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "tags", "zones"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Study> findTop9ByPublishedTrueAndClosedFalseOrderByPublishedDateTimeDesc();

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
