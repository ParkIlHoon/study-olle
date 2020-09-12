package com.studyolle.modules.notification;

import com.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
    int countByAccountAndChecked(Account account, boolean b);

    List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked);

    void deleteByAccountAndChecked(Account account, boolean checked);
}
