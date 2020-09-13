package com.studyolle.modules.study;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.studyolle.modules.study.QStudy.study;

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
    public List<Study> findByKeyword(String keyword)
    {
        JPQLQuery<Study> query = from(study)
                                .where(study.published.isTrue()
                                        .and(study.title.containsIgnoreCase(keyword))
                                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword))
                                    );
        return query.fetch();
    }
}
