package com.fanxin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanxin.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    void addUser(String nickname, String email, String password);

    void updateLoginTime(String email);

    void retrieve(String email, String md5String);
}
