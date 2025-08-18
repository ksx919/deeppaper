package com.fanxin.controller;

import com.fanxin.Service.user.LoginService;
import com.fanxin.common.CommonResult;
import com.fanxin.entity.dto.LoginDTO;
import com.fanxin.entity.dto.RegisterDTO;
import com.fanxin.entity.dto.RetrieveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户注册、登录等相关接口")
public class UserController {
    @Autowired
    private LoginService loginService;

    /**
    获取验证码
     */
    @PostMapping("/getcode")
    @Operation(summary = "获取验证码", description = "通过邮箱获取验证码")
    public CommonResult<?> getCode(@Parameter(name = "email", description = "用户邮箱", required = true) @RequestParam String email,
                                   @Parameter(name = "type", description = "验证码类型，0为注册，1为找回密码", required = true) @RequestParam short type){
        loginService.getCode(email,type);
        return CommonResult.success("验证码发送成功！");
    }

    /**
    注册账号
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户通过邮箱验证码进行注册")
    public CommonResult<?> register(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "注册信息", required = true, content = @Content(schema = @Schema(implementation = RegisterDTO.class))) @RequestBody @Validated RegisterDTO registerDTO){
        return CommonResult.success(loginService.register(registerDTO));
    }

    /**
    登陆账号
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过邮箱和密码进行登录")
    public CommonResult<?> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "登录信息", required = true, content = @Content(schema = @Schema(implementation = LoginDTO.class))) @RequestBody @Validated LoginDTO loginDTO){
        String token = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return CommonResult.success(token);
    }

    /**
     * 找回密码
     */
    @PostMapping("/retrieve")
    @Operation(summary = "用户找回密码", description = "用户通过邮箱和验证码进行找回密码（用于验证找回密码的验证码）")
    public CommonResult<?> retrieve(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "找回密码信息", required = true, content = @Content(schema = @Schema(implementation = RetrieveDTO.class))) @RequestBody RetrieveDTO retrieveDTO){
        loginService.retrieve(retrieveDTO);
        return CommonResult.success();
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出登录", description = "用户退出登录")
    public CommonResult<?> logout(String token){
        loginService.logout(token);
        return CommonResult.success();
    }
}
