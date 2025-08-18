package com.fanxin.enums;

import com.fanxin.exception.ErrorCode;

public interface LoginErrorCodeConstants {
    // ========== 参数配置 1-001-000 ==========
    //验证码发送失败
    ErrorCode CODE_SEND_ERROR = new ErrorCode(1001001, "验证码发送失败");
    //验证码已过期
    ErrorCode CODE_EXPIRED = new ErrorCode(1001002, "验证码已过期");
    //验证码错误
    ErrorCode CODE_ERROR = new ErrorCode(1001003, "验证码错误");
    //用户已注册
    ErrorCode USER_REGISTERED = new ErrorCode(1001004, "用户已注册");
    //用户不存在
    ErrorCode USER_NOT_EXIST = new ErrorCode(1001005, "用户不存在");
    //密码错误
    ErrorCode PASSWORD_ERROR = new ErrorCode(1001006, "密码错误");
    //Jwt格式错误
    ErrorCode JWT_FORMAT_ERROR = new ErrorCode(1001007, "Jwt格式错误");
    //JwtToken失效
    ErrorCode JWT_TOKEN_EXPIRED = new ErrorCode(1001008, "JwtToken失效");

}
