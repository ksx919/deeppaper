package com.fanxin.enums;

import com.fanxin.exception.ErrorCode;

public interface PaperErrorCodeConstants {
    // ========== 参数配置 1-003-000 ==========
    //论文上传失败
    ErrorCode PAPER_UPLOAD_FAILED = new ErrorCode(1003001, "论文上传失败");
    //论文不存在
    ErrorCode PAPER_NOT_EXIST = new ErrorCode(1003002, "论文不存在");
    //无权限删除论文
    ErrorCode PAPER_NO_PERMISSION_DELETE = new ErrorCode(1003003, "无权限删除论文");
    //系统出错，删除失败
    ErrorCode PAPER_DELETE_FAILED = new ErrorCode(1003004, "系统出错，删除失败");
}
