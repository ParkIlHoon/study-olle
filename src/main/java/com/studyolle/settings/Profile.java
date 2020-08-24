package com.studyolle.settings;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * <h1>사용자 프로필 수정 폼 클래스</h1>
 */
@Data
@NoArgsConstructor
public class Profile
{
    /**
     * 사용자 정보
     */
    @Length(max = 35)
    private String bio;

    /**
     * 사용자 정보 - url
     */
    private String url;

    /**
     * 사용자 정보 - 소속
     */
    private String occupation;

    /**
     * 사용자 정보 - 위치
     */
    private String location;

    /**
     * 사용자 정보 - 이미지
     */
    private String profileImage;
}
