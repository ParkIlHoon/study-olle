package com.studyolle.utils;

import lombok.Builder;
import lombok.Data;

/**
 * <h1>메일 메시지 객체</h1>
 */
@Data
@Builder
public class EmailMessage
{
    private String to;
    private String subject;
    private String message;
}
