package com.studyolle.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h1>알림 서비스 클래스</h1>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService
{
    private final NotificationRepository notificationRepository;

    /**
     * 알림 읽음 처리
     * @param notifications
     */
    public void markAsRead(List<Notification> notifications)
    {
        for (Notification notification : notifications)
        {
            notification.setChecked(true);
        }

        notificationRepository.saveAll(notifications);
    }
}
