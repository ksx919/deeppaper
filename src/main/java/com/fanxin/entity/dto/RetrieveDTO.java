package com.fanxin.entity.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "找回密码信息传输对象")
public class RetrieveDTO {
    @Schema(description = "用户邮箱", required = true, example = "user@example.com")
    private String email;
    @Schema(description = "验证码", required = true, example = "123456")
    private String code;
    @Schema(description = "用户密码", required = true, example = "password123")
    private String password;
}