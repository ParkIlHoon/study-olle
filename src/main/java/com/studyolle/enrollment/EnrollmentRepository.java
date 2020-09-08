package com.studyolle.enrollment;

import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>
{
    boolean existsByEventAndAccount(Event event, Account account);

    Optional<Enrollment> findByEventAndAccount(Event event, Account account);
}
