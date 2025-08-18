package com.fanxin.Service.user.impl;

import com.fanxin.Service.user.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendCodeEmail(String to, String code) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail, "DeepPaper验证码"); // 发件人别名
        helper.setTo(to);
        helper.setSubject("您的验证码");
        helper.setText("验证码: <b>" + code + "</b>，5分钟内有效", true); // HTML格式

        mailSender.send(message);
    }
}
