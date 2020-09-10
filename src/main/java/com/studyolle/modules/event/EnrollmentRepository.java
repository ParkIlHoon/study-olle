package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Enrollment;
import com.studyolle.modules.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>
{
    boolean existsByEventAndAccount(Event event, Account account);

    Optional<Enrollment> findByEventAndAccount(Event event, Account account);
}
