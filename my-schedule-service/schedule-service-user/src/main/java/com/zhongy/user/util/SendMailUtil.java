package com.zhongy.user.util;

import entity.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMailUtil {
    @Value("${spring.mail.from}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 发送简单邮件
     */
    public boolean send(Mail mail) {
        String to = mail.getTo();
        String title = mail.getTitle();
        String content = mail.getContent();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);

        try {
            javaMailSender.send(message);
            log.info("邮件发送成功");
            return true;
        } catch (MailException e) {
            log.error("邮件发送失败, to: {}, title: {}", to, title, e);
            return false;
      }
    }
}
