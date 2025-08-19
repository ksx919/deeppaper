package com.fanxin.controller;

import com.fanxin.Service.minio.MinioService;
import com.fanxin.common.CommonResult;
import com.fanxin.util.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.fanxin.common.ServiceExceptionUtil.exception;
import static com.fanxin.enums.UserErrorCodeConstants.UPLOAD_AVATAR_ERROR;

@RestController
@RequestMapping("/user")
@Tag(name = "用户接口" ,description = "获取用户信息等与用户相关接口")
public class UserController {

    @Autowired
    private MinioService minioService;
    /**
     * 上传用户头像
     */
    @PostMapping("/upload")
    public CommonResult<String> uploadAvatar(@RequestParam MultipartFile file,short type){
        try {
            String url = minioService.uploadFile(file,type);
            return CommonResult.success(url);
        } catch (Exception e) {
            throw exception(UPLOAD_AVATAR_ERROR);
        }
    }
}
