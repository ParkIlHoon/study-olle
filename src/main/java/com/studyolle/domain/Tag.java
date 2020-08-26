package com.studyolle.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <h1>태그 엔티티 클래스</h1>
 */
@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Tag
{
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
}
