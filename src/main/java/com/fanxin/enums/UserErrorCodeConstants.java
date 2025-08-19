package com.fanxin.enums;

import com.fanxin.exception.ErrorCode;

public interface UserErrorCodeConstants {
    // ========== 参数配置 1-002-000 ==========
    //上传头像失败
    ErrorCode UPLOAD_AVATAR_ERROR = new ErrorCode(100200001, "上传头像失败");
}
