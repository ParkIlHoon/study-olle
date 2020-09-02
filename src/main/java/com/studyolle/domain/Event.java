package com.studyolle.domain;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@NamedEntityGraph(name = "Event.withEnrollment", attributeNodes = {
        @NamedAttributeNode("enrollments")
})
public class Event
{
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = true)
    private int limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserDetails account)
    {
        return false;
    }
    public boolean isDisenrollableFor(UserDetails account)
    {
        return false;
    }
    public boolean isAttended(UserDetails account)
    {
        return false;
    }
    public boolean canReject(Enrollment enrollment)
    {
        return false;
    }
    public boolean canAccept(Enrollment enrollment)
    {
        return false;
    }

    public int numberOfRemainSpots ()
    {
        return this.limitOfEnrollments - (int) enrollments.stream().filter(e -> e.isAccepted()).count();
    }
}
