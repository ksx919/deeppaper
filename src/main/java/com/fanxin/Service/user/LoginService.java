package com.fanxin.Service.user;

import com.fanxin.entity.dto.RegisterDTO;
import com.fanxin.entity.dto.RetrieveDTO;

public interface LoginService {
    void getCode(String email,short type);

    String register(RegisterDTO registerDTO);

    String login(String email, String password);

    void updateLoginTime(String email);

    void retrieve(RetrieveDTO retrieveDTO);

    void logout(String token);
}
