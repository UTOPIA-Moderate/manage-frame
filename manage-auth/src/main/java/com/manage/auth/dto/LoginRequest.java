package com.manage.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "登录类型不能为空")
    private String type;

    private String userName;
    private String password;
    private String phone;
    private String verifyCode;
}
