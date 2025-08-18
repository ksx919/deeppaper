package com.fanxin.entity.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "注册信息传输对象")
public class RegisterDTO {
    @Schema(description = "用户邮箱", required = true, example = "user@example.com")
    @Email
    private String email;
    @Schema(description = "验证码", required = true, example = "123456")
    private String code;
    @Schema(description = "用户密码", required = true, example = "password123")
    private String password;
    @Schema(description = "用户昵称", required = true, example = "张三")
    private String nickname;
}
