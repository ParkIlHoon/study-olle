package com.studyolle.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Enrollment
{
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attend;
}
