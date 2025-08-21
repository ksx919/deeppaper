package com.fanxin.controller;

import com.fanxin.Service.minio.MinioService;
import com.fanxin.common.CommonResult;
import com.fanxin.entity.User;
import com.fanxin.util.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    @Operation(summary = "上传用户头像", description = "上传用户头像")
    public CommonResult<String> uploadAvatar(@Parameter(name = "file", description = "用户头像", required = true) @RequestParam MultipartFile file){
        try {
            String url = minioService.uploadAvatarFile(file);
            return CommonResult.success(url);
        } catch (Exception e) {
            return CommonResult.error(UPLOAD_AVATAR_ERROR);
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "获取用户信息")
    public CommonResult<User> getUserProfile(){
        Map<String,Object> claims= ThreadLocalUtil.get();
        User curUser = new User();
        curUser.setId(((Integer) claims.get("id")).longValue());
        curUser.setNickname(claims.get("nickname").toString());
        curUser.setEmail(claims.get("email").toString());
        curUser.setAvatar(claims.get("avatar").toString());
        return CommonResult.success(curUser);
    }
}
