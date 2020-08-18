package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h1>사용자 계정 엔티티 클래스</h1>
 */
@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account
{
    /**
     * 사용자 아이디
     */
    @Id @GeneratedValue
    private Long id;

    /**
     * 사용자 이메일
     */
    @Column(unique = true)
    private String email;

    /**
     * 사용자 닉네임
     */
    @Column(unique = true)
    private String nickname;

    /**
     * 사용자 패스워드
     */
    private String password;

    /**
     * 이메일 인증 여부
     */
    private boolean emailVerified;

    /**
     * 이메일 인증체크 토큰 값
     */
    private String emailCheckToken;

    /**
     * 가입일
     */
    private LocalDateTime joinedAt;

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

    /**
     * 사용자 정보 - 프로필 이미지
     */
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    /**
     * 스터디 생성 이메일 알림 여부
     */
    private boolean studyCreatedByEmail;

    /**
     * 스터디 생성 웹 알림 여부
     */
    private boolean studyCreatedByWeb;

    /**
     * 스터디 신청결과 이메일 알림 여부
     */
    private boolean studyEnrollmentResultByEmail;

    /**
     * 스터디 신청경과 웹 알림 여부
     */
    private boolean studyEnrollmentResultByWeb;

    /**
     * 스터디 업데이트 시 이메일 알림 여부
     */
    private boolean studyUpdatedByEmail;

    /**
     * 스터디 업데이트 시 웹 알림 여부
     */
    private boolean studyUpdatedByWeb;

    public void generateEmailCheckToken()
    {
        this.emailCheckToken = UUID.randomUUID().toString();
    }


    /**
     * 사용자 인증 메서드
     */
    public void verifyEmail()
    {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }
}
