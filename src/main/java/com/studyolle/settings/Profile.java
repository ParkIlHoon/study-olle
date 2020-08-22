package com.studyolle.settings;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

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



    public Profile(Account account)
    {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
