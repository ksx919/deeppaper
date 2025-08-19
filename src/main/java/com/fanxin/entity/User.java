package com.fanxin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginTime;
}
