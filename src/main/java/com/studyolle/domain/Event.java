package com.studyolle.domain;

import com.studyolle.account.UserAccount;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserAccount account)
    {
        return isNotClosed() && !isAttended(account) && !isAlreadyEnrolled(account);
    }
    public boolean isDisenrollableFor(UserAccount account)
    {
        return isNotClosed() && !isAttended(account) && isAlreadyEnrolled(account);
    }

    private boolean isAlreadyEnrolled(UserAccount account)
    {
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account.getAccount())) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotClosed()
    {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isAttended(UserAccount account)
    {
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account.getAccount()) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }
    public boolean canAccept(Enrollment enrollment)
    {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment)
    {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public int numberOfRemainSpots ()
    {
        return this.limitOfEnrollments - getNumberOfAcceptedEnrollments();
    }

    public int getNumberOfAcceptedEnrollments()
    {
        return (int) enrollments.stream().filter(e -> e.isAccepted()).count();
    }

    public boolean isAbleToAcceptWaitingEnrollment()
    {
        // 모임이 선착순 모임이고, 참가확정된 인원이 모집 인원보다 적을 때 true
        return this.eventType == EventType.FCFS
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    public void addEnrollment(Enrollment enrollment)
    {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment)
    {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public Enrollment getTheFirstWaitingEnrollment()
    {
        for (Enrollment enrollment : this.enrollments)
        {
            if (!enrollment.isAccepted())
            {
                return enrollment;
            }
        }
        return null;
    }

    public void acceptTheFirstWaitingEnrollment()
    {
        if (this.isAbleToAcceptWaitingEnrollment())
        {
            Enrollment firstWaiting = this.getTheFirstWaitingEnrollment();
            if (firstWaiting != null)
            {
                firstWaiting.setAccepted(true);
            }
        }
    }
}
