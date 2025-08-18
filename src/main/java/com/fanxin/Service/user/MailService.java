package com.fanxin.Service.user;

public interface MailService {
    void sendCodeEmail(String to, String code) throws Exception;
}
