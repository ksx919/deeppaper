package com.fanxin.Service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanxin.Service.user.LoginService;
import com.fanxin.Service.user.MailService;
import com.fanxin.entity.dto.RetrieveDTO;
import com.fanxin.mapper.UserMapper;
import com.fanxin.entity.User;
import com.fanxin.entity.dto.RegisterDTO;
import com.fanxin.util.JwtUtil;
import com.fanxin.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.fanxin.common.ServiceExceptionUtil.exception;
import static com.fanxin.enums.LoginErrorCodeConstants.*;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private MailService mailService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getCode(String email,short type) {
        try {
            final String code = String.valueOf(new Random().nextInt(899999) + 100000);
            String key;
            if(type == 0){
                key = "captcha:register:email:" + email;
            }else {
                User u = selectUserByEmail(email);
                if(u == null){
                    throw exception(USER_NOT_EXIST);
                }
                key = "captcha:retrieve:email:" + email;
            }
            stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
            mailService.sendCodeEmail(email, code);
        } catch (Exception e) {
            throw exception(CODE_SEND_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterDTO registerDTO) {
        String key = "captcha:register:email:" + registerDTO.getEmail();
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (storedCode == null) {
            throw exception(CODE_EXPIRED);
        }else if (!storedCode.equals(registerDTO.getCode())) {
            throw exception(CODE_ERROR);
        }
        User u = selectUserByEmail(registerDTO.getEmail());
        if (u != null) {
            throw exception(USER_REGISTERED);
        }else{
            String md5String = MD5Utils.string2MD5(registerDTO.getPassword());
            userMapper.addUser(registerDTO.getNickname(),registerDTO.getEmail(),md5String);
            Map<String,Object> claims = new HashMap<>();
            claims.put("email",registerDTO.getEmail());
            claims.put("nickname",registerDTO.getNickname());
            claims.put("id", selectUserByEmail(registerDTO.getEmail()));
            String token = JwtUtil.genToken(claims);
            stringRedisTemplate.opsForValue().set(token,token,1,TimeUnit.DAYS);
            stringRedisTemplate.delete("captcha:register:email:"+registerDTO.getEmail());
            updateLoginTime(registerDTO.getEmail());
            return token;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String login(String email, String password) {
        User loginUser = selectUserByEmail(email);
        if (loginUser == null){
            throw exception(USER_NOT_EXIST);
        }
        if (MD5Utils.passwordIsTrue(password,loginUser.getPassword())){
            //登录成功
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("nickname",loginUser.getNickname());
            claims.put("email",loginUser.getEmail());
            claims.put("avatar",loginUser.getAvatar());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.DAYS);
            updateLoginTime(loginUser.getEmail());
            return token;
        }else {
            throw exception(PASSWORD_ERROR);
        }
    }

    @Override
    public void updateLoginTime(String email) {
        userMapper.updateLoginTime(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retrieve(RetrieveDTO retrieveDTO) {
        String email = retrieveDTO.getEmail();
        String code = retrieveDTO.getCode();
        String key = "captcha:retrieve:email:" + email;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (storedCode == null) {
            throw exception(CODE_EXPIRED);
        }else if (!storedCode.equals(code)) {
            throw exception(CODE_ERROR);
        }
        stringRedisTemplate.delete(key);
        String md5String = MD5Utils.string2MD5(retrieveDTO.getPassword());
        userMapper.retrieve(retrieveDTO.getEmail(),md5String);
    }

    @Override
    public void logout(String token) {
        stringRedisTemplate.delete(token);
    }

    public User selectUserByEmail(String email){
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail,email));
    }
}
