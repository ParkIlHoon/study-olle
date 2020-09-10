package com.studyolle.modules.account;

import lombok.Data;

/**
 * <h1>알림 설정 폼 객체</h1>
 */
@Data
public class Notifications
{
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;
}
