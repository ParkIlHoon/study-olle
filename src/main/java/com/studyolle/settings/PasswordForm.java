package com.studyolle.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * <h1>사용자 패스워드 변경 폼 클래스</h1>
 */
@Data
public class PasswordForm
{
    @NotBlank
    @Length(min = 8, max = 50)
    private String newPassword;

    @NotBlank
    @Length(min = 8, max = 50)
    private String newPasswordConfirm;
}
