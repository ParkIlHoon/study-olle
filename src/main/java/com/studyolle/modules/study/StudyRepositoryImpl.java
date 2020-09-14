package com.studyolle.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

import static com.studyolle.modules.account.QAccount.account;
import static com.studyolle.modules.study.QStudy.study;
import static com.studyolle.modules.tag.QTag.tag;
import static com.studyolle.modules.zone.QZone.zone;

public class StudyRepositoryImpl extends QuerydslRepositorySupport implements StudyRepositoryCustom
{
    /**
     * 상위 클래스에 기본 생성자가 없어 생성
     */
    public StudyRepositoryImpl()
    {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable)
    {
        JPQLQuery<Study> query = from(study)
                                .where(study.published.isTrue()
                                        .and(study.title.containsIgnoreCase(keyword))
                                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword))
                                    )
                                .leftJoin(study.tags, tag).fetchJoin()
                                .leftJoin(study.zones, zone).fetchJoin()
                                .leftJoin(study.members, account).fetchJoin()
                                .distinct();

        JPQLQuery<Study> pagination = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> studyQueryResults = pagination.fetchResults();

        return new PageImpl<>(studyQueryResults.getResults(), pageable, studyQueryResults.getTotal());
    }

    @Override
    public List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones)
    {
        JPQLQuery<Study> jpqlQuery = from(study)
                                    .where(
                                            study.published.isTrue()
                                                    .and(study.closed.isFalse())
                                                    .and(study.tags.any().in(tags))
                                                    .and(study.zones.any().in(zones))
                                            )
                                    .leftJoin(study.tags, tag).fetchJoin()
                                    .leftJoin(study.zones, zone).fetchJoin()
                                    .orderBy(study.publishedDateTime.desc())
                                    .distinct()
                                    .limit(9);

        return jpqlQuery.fetch();
    }
}