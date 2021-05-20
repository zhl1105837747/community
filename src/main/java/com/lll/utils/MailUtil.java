package com.lll.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author 龙儿
 */
@Component
public class MailUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("1105837747@qq.com")
    private String from;

    /**
     * @param to      收件人
     * @param subject 主题
     * @param content 文本内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper help = new MimeMessageHelper(mimeMessage);
            help.setFrom(from);
            help.setTo(to);
            help.setSubject(subject);
            help.setText(content,true);
            mailSender.send(help.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败："+e.getMessage());
        }

    }

}
