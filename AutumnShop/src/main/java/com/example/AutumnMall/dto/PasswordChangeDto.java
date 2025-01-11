package com.example.AutumnMall.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PasswordChangeDto {
    @NotBlank(message = " 기존 비밀번호를 입력하세요.")
    private String oldPassword;

    @NotBlank(message = " 새 비밀번호를 입력하세요.")
    private String newPassword;
}
